package br.gov.mec.aghu.exames.patologia.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoNaoAplicavel;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.AelKitMatPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelDescMaterialAps;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelKitItemMatPatologia;
import br.gov.mec.aghu.model.AelKitMatPatologia;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class LaudoLaudoUnicoController extends ActionController  {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(LaudoLaudoUnicoController.class);

	private static final long serialVersionUID = 136429204303812851L;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@Inject
	private LaudoUnicoController laudoUnicoController;

	private TelaLaudoUnicoVO tela;
	private AelKitMatPatologia kitMaterial;
	
	private AelInformacaoClinicaAP informacaoClinica;
	private AelMacroscopiaAps macroscopia;
	private AelDiagnosticoAps diagnostico;
	private AelDescMaterialAps descMaterialLaudo;
	
	private String strInformacaoClinica;
	private String strDescricaoMaterialLaudo;
	private String strMacroscopia;
	private String strDiagnostico;
	
	//usado no materiais AP
	private List<AelKitMatPatologiaVO> listaMateriaisVO = null;
	private AelMaterialAp materialAp;
	private AelMaterialAp excluirMaterialAp = null;
	private String descricaoMaterial;
	private AelKitMatPatologiaVO kitMatPatologiaSelecionado;
	
	// Atributos de diagnóstico
	private DominioSimNao neoplasiaMaligna;
	private DominioSimNaoNaoAplicavel margemComprometida;
	private DominioSimNao biopsia;
	
	private Boolean editando;
	private Boolean primeiraVez = Boolean.TRUE;
	
	private Boolean gravaMacroscopia = Boolean.FALSE;
	private Boolean gravaDiagnostico = Boolean.FALSE;
	private Boolean gravaMaterial = Boolean.FALSE;
	
	private Integer materialDataTableHeight;
	
	public void inicio(final TelaLaudoUnicoVO tela) {
		this.tela = tela;
		
		editando = Boolean.FALSE;
		
		if(tela.getAelExameAp() != null) {
			
			// chamar postquery lux
			
			inicializarMaterialAp();
			
			if (primeiraVez) {
				informacaoClinica = examesPatologiaFacade.obterAelInformacaoClinicaApPorAelExameAps(tela.getAelExameAp().getSeq());
				macroscopia = examesPatologiaFacade.obterAelMacroscopiaApsPorAelExameAps(tela.getAelExameAp().getSeq());
				descMaterialLaudo = examesPatologiaFacade.obterAelDescMaterialApsPorAelExameAps(tela.getAelExameAp().getSeq());
				diagnostico = examesPatologiaFacade.obterAelDiagnosticoApsPorAelExameAps(tela.getAelExameAp().getSeq());	
				
				if (informacaoClinica != null) {
					carregarAtributosInformacaoClinica();
				}
				
				if (macroscopia != null) {
					carregarAtributosMacroscopia();
				}
				
				if (descMaterialLaudo != null) {
					carregarAtributosDescMaterialLaudo();
				}
				
				if (diagnostico != null) {
					carregarAtributosDiagnostico();		
				}
			}
		}
		
		primeiraVez = Boolean.FALSE;
	}

	public void limpar() {
		kitMaterial = null;
		
		informacaoClinica = null;
		macroscopia = null;
		descMaterialLaudo = null;
		diagnostico = null;
		listaMateriaisVO = null;
		
		strInformacaoClinica = null;
		strDescricaoMaterialLaudo = null;
		strMacroscopia = null;
		strDiagnostico = null;		
		
		materialAp = null;
		excluirMaterialAp = null;
		descricaoMaterial = null;
		
		primeiraVez = Boolean.TRUE;
		
		neoplasiaMaligna = null;
		margemComprometida = null;
		biopsia = null;		
	}
	
	public List<AelKitMatPatologia> listarKitMaterialAp() {
		return examesPatologiaFacade.pesquisarAelKitMatPatologia(null, null, DominioSituacao.A);
	}
	
	private void obterlistaMateriais(){
		listaMateriaisVO = examesPatologiaFacade.listaMateriais(tela.getAelExameAp().getSeq(), tela.getAelExameAp().getEtapasLaudo());
		
		setMaterialDataTableHeight(laudoUnicoController.dataTableSize(listaMateriaisVO));
	}

	private void inicializarMaterialAp() {
		//Pesquisa material para colocar na tabela
		if (listaMateriaisVO == null) {
			this.obterlistaMateriais();
		}
		materialAp = new AelMaterialAp();
		materialAp.setAelExameAp(tela.getAelExameAp());
	}
	
	private void atualizaMateriaisVO() {
		examesPatologiaFacade.atualizaMateriaisVO(listaMateriaisVO);
		setMaterialDataTableHeight(laudoUnicoController.dataTableSize(listaMateriaisVO));
	}


	public void editarKitMateriais() {
		this.materialAp = kitMatPatologiaSelecionado.getMaterialAp();
		this.descricaoMaterial = materialAp.getMaterial();
		
		editando = Boolean.TRUE;
	}
	
	public void upKitMateriais() {
		List<AelMaterialAp> listaAnterior = examesPatologiaFacade.listarAelMaterialApPorLuxSeqEOrdem(kitMatPatologiaSelecionado.getMaterialAp().getAelExameAp().getSeq(), (short) (kitMatPatologiaSelecionado.getMaterialAp().getOrdem() - 1));
		AelMaterialAp materialApAnterior = listaAnterior.get(0);
		materialApAnterior.setOrdem((short) (materialApAnterior.getOrdem() + 1));
		this.materialAp = materialApAnterior;
		descricaoMaterial = materialAp.getMaterial();
		gravarKitMaterial(false);
		
		this.materialAp = kitMatPatologiaSelecionado.getMaterialAp();
		descricaoMaterial = materialAp.getMaterial();
		materialAp.setOrdem((short) (materialAp.getOrdem() - 1));
		gravarKitMaterial(false);	
	}

	
	public void downKitMateriais() {
		List<AelMaterialAp> listaAnterior = examesPatologiaFacade.
				listarAelMaterialApPorLuxSeqEOrdem(kitMatPatologiaSelecionado.getMaterialAp().getAelExameAp().getSeq(), 
						(short) (kitMatPatologiaSelecionado.getMaterialAp().getOrdem() + 1));
		
		AelMaterialAp materialApAnterior = listaAnterior.get(0);
		materialApAnterior.setOrdem((short) (materialApAnterior.getOrdem() - 1));
		this.materialAp = materialApAnterior;
		descricaoMaterial = materialAp.getMaterial();
		gravarKitMaterial(false);
		
		this.materialAp = kitMatPatologiaSelecionado.getMaterialAp();
		descricaoMaterial = materialAp.getMaterial();
		materialAp.setOrdem((short) (materialAp.getOrdem() + 1));
		gravarKitMaterial(false);	
	}	
	
	public void gravarKitMaterial(boolean mostrarMensagem){
		try {			
			materialAp.setMaterial(descricaoMaterial);
			boolean isInsert = materialAp.getSeq() == null;
			if (materialAp.getOrdem() == null) {
				materialAp.setOrdem((short) (listaMateriaisVO.size() + 1));
			}	
			
			//Obter o materialAP original
			AelMaterialAp materialAPOld =  this.examesPatologiaFacade.obterAelMaterialApPorSeq(tela.getAelExameAp().getSeq()); 
			
			examesPatologiaFacade.persistirAelMaterialAp(materialAp, materialAPOld, isInsert);
			//examesPatologiaFacade.refresh(materialAp);
			if (isInsert) {
				listaMateriaisVO.add(new AelKitMatPatologiaVO(materialAp, null, tela.getAelExameAp().getEtapasLaudo()));
				setMaterialDataTableHeight(laudoUnicoController.dataTableSize(listaMateriaisVO));
			}
			
			if (mostrarMensagem) {
				if(isInsert){
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INSERT_MATERIAL_AP");
				} else {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_UPDATE_MATERIAL_AP");
				}
			}

			editando = Boolean.FALSE;
			atualizaMateriaisVO();
		}
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		descricaoMaterial = null;
		kitMaterial = null;
		materialAp = new AelMaterialAp();
		materialAp.setAelExameAp(tela.getAelExameAp());
	}
	
	public void excluirKitMateriaisLista() {
		this.excluirMaterialAp = kitMatPatologiaSelecionado.getMaterialAp();
		if(excluirMaterialAp != null){
			this.excluirKitMateriais();
		}
	}

	public void excluirKitMateriais() {
		if (excluirMaterialAp != null) {
			try {
				Long luxSeq = excluirMaterialAp.getAelExameAp().getSeq();
				examesPatologiaFacade.excluirAelMaterialAp(excluirMaterialAp);
				
				for (AelKitMatPatologiaVO vo : listaMateriaisVO) {
					if (vo.getMaterialAp().getSeq().equals(excluirMaterialAp.getSeq())) {
						listaMateriaisVO.remove(vo);
						break;
					}
				}
				
				List<AelMaterialAp> listaMaterial = examesPatologiaFacade.listarAelMaterialApPorLuxSeqEOrdemMaior(luxSeq, excluirMaterialAp.getOrdem());
				for (AelMaterialAp item : listaMaterial) {
					AelMaterialAp itemOld = item; 
					item.setOrdem((short) (item.getOrdem() - 1));
					examesPatologiaFacade.persistirAelMaterialAp(item, itemOld);
				}
				
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DELETE_MATERIAL_AP");
				
				atualizaMateriaisVO();
				excluirMaterialAp = null;
			}
			catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void ativarInativarKitMateriais() {
		if (Boolean.TRUE.equals(kitMatPatologiaSelecionado.getImunoHist())) {
			kitMatPatologiaSelecionado.setImunoHist(Boolean.FALSE);
		}
		else {
			kitMatPatologiaSelecionado.setImunoHist(Boolean.TRUE);
		}

		if (!DominioSituacaoExamePatologia.LA.equals(tela.getAelExameAp().getEtapasLaudo())) { //se nao tiver assinado
			atualizaMateriaisVO();
		}
		else { //se tiver assinado
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			try {
				List<AelKitMatPatologiaVO> listaMateriais = new ArrayList<AelKitMatPatologiaVO>();
				listaMateriais.add(kitMatPatologiaSelecionado);
				examesPatologiaFacade.ativarInativarImunoHistoquimica(listaMateriais, nomeMicrocomputador);
			

				if (Boolean.TRUE.equals(kitMatPatologiaSelecionado.getImunoHist())) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_CANCELOU_EXAME_LAUDO");
				}
				else {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ESTORNOU_CANCELAMENTO_EXAME_LAUDO");
				}
				
				
			} catch (BaseException e) {
				//se der exceção volta ao estado anterior
				if (Boolean.TRUE.equals(kitMatPatologiaSelecionado.getImunoHist())) {
					kitMatPatologiaSelecionado.setImunoHist(Boolean.FALSE);
				}
				else {
					kitMatPatologiaSelecionado.setImunoHist(Boolean.TRUE);
				}

				apresentarExcecaoNegocio(e);				
			}				
		}
	}
	
	public void adicionaMaterial() {
		boolean inseriu = false;
		if (kitMaterial != null) {
			List<AelKitItemMatPatologia> listaItens = examesPatologiaFacade.pesquisarAelKitItemMatPatologia(kitMaterial);
			for (AelKitItemMatPatologia item : listaItens) {
				if (DominioSituacao.A.equals(item.getIndSituacao())) {
					descricaoMaterial = item.getDescricao();
					gravarKitMaterial(false);
					inseriu = true;
				}
			}
			if (inseriu) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INSERT_MATERIAL_AP");
			}
			else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_MATERIAL_AP");
			}
		}
	}
	
	public void cancelarEdicaoMaterial() {
		editando = Boolean.FALSE;
		descricaoMaterial = null;		
	}
	
	public void gravarInformacaoClinica(){
		try { 
			
			if (informacaoClinica == null) {
				informacaoClinica = new AelInformacaoClinicaAP();
				informacaoClinica.setAelExameAp(tela.getAelExameAp());
			}
			
			informacaoClinica.setInformacaoClinica(strInformacaoClinica);
			
			final boolean isInsert = (informacaoClinica.getSeq() == null);

			
			examesPatologiaFacade.persistirAelInformacaoClinicaAP(informacaoClinica);
			tela.setAelExameAp(examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(tela.getAelAnatomoPatologico()));
			if (tela.getAelAnatomoPatologicoOrigem() == null) {
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(null);
			}
			else {
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(tela.getAelExameAp().getAelAnatomoPatologicoOrigem());
			}
			examesPatologiaFacade.persistirAelExameAp(tela.getAelExameAp());
		//	examesPatologiaFacade.refresh(informacaoClinica);
			
			if(isInsert){
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INSERT_INFORMACAO_CLINICA_AP");
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_UPDATE_INFORMACAO_CLINICA_AP");
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	public void gravarDescricaoMaterialLaudo(){
		try {
			if (descMaterialLaudo == null) {
				descMaterialLaudo = new AelDescMaterialAps();
				descMaterialLaudo.setAelExameAps(tela.getAelExameAp());
			}

			descMaterialLaudo.setDescrMaterial(strDescricaoMaterialLaudo);
			examesPatologiaFacade.validarDescMaterialPreenchida(descMaterialLaudo);
			examesPatologiaFacade.persistirAelDescMaterialAps(descMaterialLaudo);
			tela.setAelExameAp(examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(tela.getAelAnatomoPatologico()));
			if (tela.getAelAnatomoPatologicoOrigem() == null) {
				
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(null);
			} else {
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(tela.getAelAnatomoPatologicoOrigem());
			}
			examesPatologiaFacade.persistirAelExameAp(tela.getAelExameAp());
			descMaterialLaudo = examesPatologiaFacade.obterAelDescMaterialApPorChavePrimaria(descMaterialLaudo.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SALVA_DESCRICAO_MATERIAL_AP");
			gravaMaterial = Boolean.TRUE;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			gravaMaterial = Boolean.FALSE;
		}
	}
	
	public void concluirDescricaoMaterialLaudo() {
		if (tela.isConcluirDescricaoMaterialLaudo()) { // se deu erro ao gravar não conclui
			gravarDescricaoMaterialLaudo();
			if (!gravaMaterial) {
				return;
			}
		}

		// necessario acessar a controller pai para setar as permissoes em caso de sucesso ao concluir
		laudoUnicoController.inicio();

		if (tela.isConcluirDescricaoMaterialLaudo()) {
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_REABERTO_DESCRICAO_MATERIAL_LAUDO_AP");
		} else {
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_CONCLUIDO_DESCRICAO_MATERIAL_LAUDO_AP");
		}
	}
	
	
	//@Restrict("#{s:hasPermission('preencherMacroscopia','persistir')}")
	public void gravarMacroscopia(){
		try {
			if (macroscopia == null) {
				macroscopia = new AelMacroscopiaAps();
				macroscopia.setAelExameAps(tela.getAelExameAp());
			}
			
			macroscopia.setMacroscopia(strMacroscopia);			
			
			examesPatologiaFacade.validarMacroscopiaPreenchida(macroscopia);
			
			examesPatologiaFacade.persistirAelMacroscopiaAps(macroscopia);
			tela.setAelExameAp(examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(tela.getAelAnatomoPatologico()));
			if (tela.getAelAnatomoPatologicoOrigem() == null) {
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(null);
			}
			else {
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(tela.getAelExameAp().getAelAnatomoPatologicoOrigem());
			}
			examesPatologiaFacade.persistirAelExameAp(tela.getAelExameAp());
			macroscopia = examesPatologiaFacade.obterAelMacroscopiaApPorChavePrimaria(macroscopia.getSeq());
			
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SALVA_MACROSCOPIA_AP");
			
			gravaMacroscopia = Boolean.TRUE;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			gravaMacroscopia = Boolean.FALSE;
		}
	}
	
	//@Restrict("#{s:hasPermission('preencherMacroscopia','persistir')}")
	public void concluirMacroscopia(){
		try {
			if (tela.isConcluirMacro()) { //se deu erro ao gravar não conclui
				gravarMacroscopia();
				if (!gravaMacroscopia) {
					return;
				}
			}		
			
			examesPatologiaFacade.concluirMacroscopiaAps( tela.getAelExameAp(), macroscopia);

			//necessario acessar a controller pai para setar as permissoes em caso de sucesso ao concluir macroscopia
			laudoUnicoController.inicio();
			
			if (tela.isConcluirMacro()) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_REABERTA_MACROSCOPIA_AP");
			}
			else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_CONCLUIDA_MACROSCOPIA_AP");
			}
			
			//((LaudoUnicoController) getComponentInstance(LaudoUnicoController.class)).direcionaMapaLaminas();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
	}
	
	
	
	//@Restrict("#{s:hasPermission('preencherDiagnosticoLaudoUnico','persistir')}")
	public void gravarDiagnostico(){
		try {
			if (diagnostico == null) {
				diagnostico = new AelDiagnosticoAps();
				diagnostico.setAelExameAp(tela.getAelExameAp());
			} 
			
			diagnostico.setDiagnostico(strDiagnostico);
			diagnostico.setNeoplasiaMaligna(neoplasiaMaligna);
			diagnostico.setMargemComprometida(margemComprometida);
			diagnostico.setBiopsia(biopsia);	
			
//			examesPatologiaFacade.validarDiagnosticoPreenchido(diagnostico);
			
			examesPatologiaFacade.persistirAelDiagnosticoAps(diagnostico);
			tela.setAelExameAp(examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(tela.getAelAnatomoPatologico()));
			if (tela.getAelAnatomoPatologicoOrigem() == null) {
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(null);
			}
			else {
				tela.getAelExameAp().setAelAnatomoPatologicoOrigem(tela.getAelExameAp().getAelAnatomoPatologicoOrigem());
			}
			examesPatologiaFacade.persistirAelExameAp(tela.getAelExameAp());
			diagnostico = examesPatologiaFacade.obterAelDiagnosticoApPorChavePrimaria(diagnostico.getSeq());
			
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SALVA_DIAGNOSTICO_AP");
			
			gravaDiagnostico = Boolean.TRUE;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			gravaDiagnostico = Boolean.FALSE;
		}
	}
	
	//@Restrict("#{s:hasPermission('preencherDiagnosticoLaudoUnico','persistir')}")
	public void concluirDiagnostico(){	
		try {
			if (tela.isConcluirDiagnostico()) { //se deu erro ao gravar não conclui
				gravarDiagnostico();
				if (!gravaDiagnostico) {
					return;
				}
			}
			
			examesPatologiaFacade.concluirDiagnosticoAps( tela.getAelExameAp(), 
														  diagnostico, 
														  examesPatologiaFacade.listarTopografiaApPorLuxSeq(tela.getAelExameAp().getSeq()), 
														  examesPatologiaFacade.listarNomenclaturaApPorLuxSeq(tela.getAelExameAp().getSeq()),
														  examesPatologiaFacade.obterListaLaminasPeloExameApSeq(tela.getAelExameAp().getSeq())
														);
			
			//necessario acessar a controller pai para setar as permissoes em caso de sucesso ao concluir diagnostico
			laudoUnicoController.inicio();
			
			if (tela.isConcluirDiagnostico()) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_REABERTO_DIAGNOSTICO_AP");
			}
			else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_CONCLUIDO_DIAGNOSTICO_AP");
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	private void carregarAtributosInformacaoClinica() {
		strInformacaoClinica = informacaoClinica.getInformacaoClinica();
	}
	
	private void carregarAtributosMacroscopia() {
		strMacroscopia = macroscopia.getMacroscopia();
	}
	
	private void carregarAtributosDescMaterialLaudo() {
		strDescricaoMaterialLaudo = descMaterialLaudo.getDescrMaterial();
	}
	
	private void carregarAtributosDiagnostico() {
		strDiagnostico = diagnostico.getDiagnostico();
		neoplasiaMaligna = diagnostico.getNeoplasiaMaligna();
		margemComprometida = diagnostico.getMargemComprometida();
		biopsia = diagnostico.getBiopsia();	
	}
	
	public AelMacroscopiaAps getMacroscopia() {
		return macroscopia;
	}

	public void setMacroscopia(AelMacroscopiaAps macroscopia) {
		this.macroscopia = macroscopia;
	}
	
	public AelDescMaterialAps getDescMaterialLaudo() {
		return descMaterialLaudo;
	}

	public void setDescMaterialLaudo(AelDescMaterialAps descMaterialLaudo) {
		this.descMaterialLaudo = descMaterialLaudo;
	}

	public List<AelKitMatPatologiaVO> getListaMateriaisVO() {
		return listaMateriaisVO;
	}

	public void setListaMateriaisVO(List<AelKitMatPatologiaVO> listaMateriaisVO) {
		this.listaMateriaisVO = listaMateriaisVO;
	}

	public AelMaterialAp getMaterialAp() {
		return materialAp;
	}

	public void setMaterialAp(AelMaterialAp materialAp) {
		this.materialAp = materialAp;
	}

	public AelKitMatPatologia getKitMaterial() {
		return kitMaterial;
	}

	public void setKitMaterial(AelKitMatPatologia kitMaterial) {
		this.kitMaterial = kitMaterial;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public AelMaterialAp getExcluirMaterialAp() {
		return excluirMaterialAp;
	}

	public void setExcluirMaterialAp(AelMaterialAp excluirMaterialAp) {
		this.excluirMaterialAp = excluirMaterialAp;
	}

	public AelDiagnosticoAps getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(AelDiagnosticoAps diagnostico) {
		this.diagnostico = diagnostico;
	}

	public AelInformacaoClinicaAP getInformacaoClinica() {
		return informacaoClinica;
	}

	public void setInformacaoClinica(AelInformacaoClinicaAP informacaoClinica) {
		this.informacaoClinica = informacaoClinica;
	}

	public TelaLaudoUnicoVO getTela() {
		return tela;
	}

	public void setTela(TelaLaudoUnicoVO tela) {
		this.tela = tela;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public String getStrInformacaoClinica() {
		return strInformacaoClinica;
	}

	public void setStrInformacaoClinica(String strInformacaoClinica) {
		this.strInformacaoClinica = strInformacaoClinica;
	}

	public String getStrMacroscopia() {
		return strMacroscopia;
	}

	public void setStrMacroscopia(String strMacroscopia) {
		this.strMacroscopia = strMacroscopia;
	}

	public String getStrDiagnostico() {
		return strDiagnostico;
	}

	public void setStrDiagnostico(String strDiagnostico) {
		this.strDiagnostico = strDiagnostico;
	}

	public Boolean getPrimeiraVez() {
		return primeiraVez;
	}

	public void setPrimeiraVez(Boolean primeiraVez) {
		this.primeiraVez = primeiraVez;
	}

	public DominioSimNao getNeoplasiaMaligna() {
		return neoplasiaMaligna;
	}

	public void setNeoplasiaMaligna(DominioSimNao neoplasiaMaligna) {
		this.neoplasiaMaligna = neoplasiaMaligna;
	}

	public DominioSimNaoNaoAplicavel getMargemComprometida() {
		return margemComprometida;
	}

	public void setMargemComprometida(DominioSimNaoNaoAplicavel margemComprometida) {
		this.margemComprometida = margemComprometida;
	}

	public DominioSimNao getBiopsia() {
		return biopsia;
	}

	public void setBiopsia(DominioSimNao biopsia) {
		this.biopsia = biopsia;
	}

	public String getStrDescricaoMaterialLaudo() {
		return strDescricaoMaterialLaudo;
	}

	public void setStrDescricaoMaterialLaudo(String strDescricaoMaterialLaudo) {
		this.strDescricaoMaterialLaudo = strDescricaoMaterialLaudo;
	}

	public AelKitMatPatologiaVO getKitMatPatologiaSelecionado() {
		return kitMatPatologiaSelecionado;
	}

	public void setKitMatPatologiaSelecionado(AelKitMatPatologiaVO kitMatPatologiaSelecionado) {
		this.kitMatPatologiaSelecionado = kitMatPatologiaSelecionado;
	}

	public Integer getMaterialDataTableHeight() {
		return materialDataTableHeight;
	}

	public void setMaterialDataTableHeight(Integer materialDataTableHeight) {
		this.materialDataTableHeight = materialDataTableHeight;
	}

}