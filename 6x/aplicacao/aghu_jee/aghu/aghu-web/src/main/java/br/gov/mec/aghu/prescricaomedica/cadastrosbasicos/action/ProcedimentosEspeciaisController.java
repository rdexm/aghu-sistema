package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.MpmProcedEspecialRmId;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimentoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ProcedimentosEspeciaisController extends ActionController {

	private static final long serialVersionUID = 2850614191587032925L;
	private static final String PAGE_PESQUISA_PROCEDIMENTOS_ESPECIAIS = "procedimentosEspeciaisList";
	private static final short ID_PROCEDIMENTO_NAO_SALVO = -1;
	//private static final Log LOG = LogFactory.getLog(ProcedimentosEspeciaisController.class);

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private SecurityController securityController;
	
	@Inject
	private RelacionarPHISSMController relacionarPHISSMController;
	
	public enum ProcedimentosEspeciaisControllerExceptionCode implements BusinessExceptionCode {
		ERRO_DESCRICAO_MODO_USO_OBRIGATORIO, 
		ERRO_SITUACAO_MODO_USO_OBRIGATORIO, 
		ERRO_MATERIAL_MATERIAIS_RM_AUTOMATICA_OBRIGATORIO, 
		ERRO_SITUACAO_MATERIAIS_RM_AUTOMATICA_OBRIGATORIO,
		ERRO_ATUALIZAR_PROCEDIMENTO_ESPECIAL;
	}

	//Variáveis para controle de edição
	private Integer codigo;
	private boolean emEdicaoModoUso;
	private Short modoUsoEmEdicaoId;
	private boolean emEdicaoMaterialRMAutomatica;
	private Integer materialRMAutomaticaEmEdicaoId;
	private MpmProcedEspecialDiversos procedimento;
	private String tempo;
	private MpmTipoModoUsoProcedimento modoUso;
	private Short maiorSeqModoUso = 0;
	private List<MpmTipoModoUsoProcedimento> modosUsos;
	private List<MpmTipoModoUsoProcedimento> modosUsosExcluidos;
	private MpmProcedEspecialRm materialRmAutomatica;
	private ScoMaterial material;
	private List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm;
	private List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidos;
	
	// 34742
	private VFatConvPlanoGrupoProcedVO tabela;
	private Short cpgGrcSeq;
	private Short cpgCphPhoSeq;
	private FatItensProcedHospitalar fatItensProcedHospitalar;
	private List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO;
	private VFatConvPlanoGrupoProcedVO vFatConvPlanoGrupoProcedVO;
	private Boolean editandoProcedimentosRelacionados;
	private List<FatProcedHospInternos> listaFatProcedHospInternos;
	private VFatConvPlanoGrupoProcedVO editarFatProcedimentoRelacionadosVO;
	private Boolean temPerfilOrdenadorDespesas;
	private Boolean chamarConfirmarSemVinculos;
	private Boolean chamarCancelar;
	protected static final String SUS = "SUS";
	
	// 38817
	private Boolean exibirPainelInferior = false;
	private Boolean alterouItensProcedimento = false;
	private VFatConvPlanoGrupoProcedVO convenio;
	private List<FatConvGrupoItemProced> convGrupoItemProcedList;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListRemover;
	private Integer phi;
	private FatConvenioSaudePlano plano;
	private FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado;
	private boolean obrigatoriedadeSigtap;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void iniciar() {
	 

		carregarModoUso();
		carregaListaMateriais();
	
	}

	private void carregarModoUso() {
		modosUsos = new ArrayList<>();
		modosUsosExcluidos = new ArrayList<>();

		if (procedimento != null) {
			modosUsos = cadastrosBasicosPrescricaoMedicaFacade.buscarModosUsoPorProcedimentoEspecial(procedimento.getSeq());
			
			if (!modosUsos.isEmpty()) {
				List<MpmTipoModoUsoProcedimento> modosUsoList = new ArrayList<MpmTipoModoUsoProcedimento>(modosUsos);
				Collections.sort(modosUsoList, new Comparator<MpmTipoModoUsoProcedimento>() {
					@Override
					public int compare(MpmTipoModoUsoProcedimento modosUso1, MpmTipoModoUsoProcedimento modosUso2) {
						return modosUso1.getDescricao().compareTo(modosUso2.getDescricao());
					}
				});
				modosUsos = modosUsoList;
			}
			
			//Verifica o maior sequencial dos modos de uso para controle dos valores temporários
			short maiorSeqInicialModoUso = 0;
			for(MpmTipoModoUsoProcedimento modoUso : procedimento.getModosProcedimentosPrescricaoDeProcedimentos()) {
				if(modoUso.getId().getSeqp() > maiorSeqInicialModoUso) {
					maiorSeqInicialModoUso = modoUso.getId().getSeqp();
				}
			}
			maiorSeqModoUso = maiorSeqInicialModoUso;
		} else {
			procedimento = new MpmProcedEspecialDiversos();
			maiorSeqModoUso = 0;
		}
		modoUso = new MpmTipoModoUsoProcedimento();
		materialRmAutomatica = new MpmProcedEspecialRm();
		
		getIniciarProcedimentosRelacionados();
	}
	
	private void getIniciarProcedimentosRelacionados() {
		obrigatoriedadeSigtap = !perfilOrdenadorDespesa();
		exibirPainelInferior = exibirPainelInferior();
		this.chamarCancelar = false;
		this.alterouItensProcedimento = false;
		this.phi = null;

		if (exibirPainelInferior){
			relacionarPHISSMController.inicio();
			convGrupoItemProcedList = new ArrayList<FatConvGrupoItemProced>();
			convGrupoItemProcedListOriginal = new ArrayList<FatConvGrupoItemProced>();
			convGrupoItemProcedListRemover = new ArrayList<FatConvGrupoItemProced>();
			if (getPhi() != null) {
				List<FatProcedHospInternos> listaProcedimentosInternos = this.faturamentoFacade.listarPhi(null, null, null, null, procedimento.getSeq(), null, null, null, null, 1);
				if (listaProcedimentosInternos != null && !listaProcedimentosInternos.isEmpty()) {
					relacionarPHISSMController.setProcedimentoInterno(listaProcedimentosInternos.get(0));
				}
				relacionarPHISSMController.pesquisarProcedimento();
				convenio = relacionarPHISSMController.obterConvenioSUS();	
				convGrupoItemProcedList = relacionarPHISSMController.getLista();
				for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedList) {
					convGrupoItemProcedListOriginal.add(fatConvGrupoItemProced);
				}
			} else {
				convenio = relacionarPHISSMController.obterConvenioSUS();
			}
		}
	}
	
	private boolean perfilOrdenadorDespesa() {
		return securityController.usuarioTemPermissao("ordenadorDespesas", "gravar");
	}

	private Boolean exibirPainelInferior() {
		Boolean exibirPanelInferior = false;
		AghParametros pExibirPainelInferior = null;
		try {
			pExibirPainelInferior = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_OBRIGATORIEDADE_SIGTAP);
		} catch (ApplicationBusinessException e) {
			//LOG.debug(e);
			apresentarExcecaoNegocio(e);
		}
		if (pExibirPainelInferior != null) {
			exibirPanelInferior = "S".equals(pExibirPainelInferior.getVlrTexto());
		}
		return exibirPanelInferior;
	}	
	
	private void carregaListaMateriais() {
		materiaisMpmProcedEspecialRm = new ArrayList<MpmProcedEspecialRm>();
		materiaisMpmProcedEspecialRmExcluidos = new ArrayList<MpmProcedEspecialRm>();
		
		if (procedimento != null) {
			List<MpmProcedEspecialRm> materiais = prescricaoMedicaFacade.listarProcedimentosRmPeloPedSeq(procedimento.getSeq());
			
			if (!materiais.isEmpty()) {
				List<MpmProcedEspecialRm> materiaisList = new ArrayList<MpmProcedEspecialRm>(materiais);
				Collections.sort(materiaisList, new Comparator<MpmProcedEspecialRm>() {
					@Override
					public int compare(MpmProcedEspecialRm material1, MpmProcedEspecialRm material2) {
						if (material1.getMaterial().getNome() == null && material2.getMaterial().getNome() != null) {
							return 1;
						}
						if (material1.getMaterial().getNome() != null && material2.getMaterial().getNome() == null) {
							return -1;
						}
						if (material1.getMaterial().getNome() == null && material2.getMaterial().getNome() == null) {
							return 0;
						}
						return material1.getMaterial().getNome().compareTo(material2.getMaterial().getNome());
					}
				});
				materiaisMpmProcedEspecialRm = materiaisList;
			}
		}

		modoUso = new MpmTipoModoUsoProcedimento();
		materialRmAutomatica = new MpmProcedEspecialRm();
		
		try{
			AghParametros pTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			this.setCpgCphPhoSeq(pTabela.getVlrNumerico().shortValue());
			List<VFatConvPlanoGrupoProcedVO> listaTabela = this.faturamentoFacade.listarTabelas(this.getCpgCphPhoSeq().toString());
			if (listaTabela != null && !listaTabela.isEmpty()) {
				this.setTabela(listaTabela.get(0));
	
				this.setCpgGrcSeq(this.getTabela().getGrcSeq());
	
			}
		} catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}	
		
		List<VFatConvPlanoGrupoProcedVO> listaConvenioInicial = this.listarConvenios(SUS);
		if(!listaConvenioInicial.isEmpty()){
			this.convenio = listaConvenioInicial.get(0);
		}
		
		this.setListaFatProcedimentoRelacionadosVO(new ArrayList<VFatConvPlanoGrupoProcedVO>());
		this.setListaFatProcedHospInternos(new ArrayList<FatProcedHospInternos>());
		this.setEditandoProcedimentosRelacionados(Boolean.FALSE);
		this.temPerfilOrdenadorDespesas = securityController.usuarioTemPermissao("procedimentoCirurgico", "ordenadorDespesas");
		this.chamarConfirmarSemVinculos = Boolean.FALSE;
		this.chamarCancelar = Boolean.FALSE;
		this.fatItensProcedHospitalar = null; 
	}

	public String gravarProdecimento() {

		// Verifica se a ação é de criação ou edição
		boolean criando = procedimento.getSeq() == null;
		List<MpmTipoModoUsoProcedimento> modosUsosAux = new ArrayList<>(modosUsos.size());
		List<MpmTipoModoUsoProcedimento> modosUsosExcAux = new ArrayList<>(modosUsosExcluidos.size());
		List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmAux = new ArrayList<>(materiaisMpmProcedEspecialRm.size());
		List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidosAux = new ArrayList<>(materiaisMpmProcedEspecialRmExcluidos.size());
		
		try {

			// Converte descricao para mauiúsculo
			procedimento.setDescricao(procedimento.getDescricao().trim().toUpperCase());

			// Verifica preenchimento do campo 'Descrição'
			if (procedimento.getDescricao().equals("")) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Descrição");
				return null;
			}
			
			clonarListas(modosUsosAux, modosUsosExcAux, materiaisMpmProcedEspecialRmAux, materiaisMpmProcedEspecialRmExcluidosAux);
			
			// Submete o procedimento para ser persistido
			cadastrosBasicosPrescricaoMedicaFacade.persistirProcedimentoEspecial(procedimento, modosUsos, modosUsosExcluidos, materiaisMpmProcedEspecialRm, materiaisMpmProcedEspecialRmExcluidos);

			// Apresenta as mensagens de acordo
			if (criando) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PROCEDIMENTO_ESPECIAL", procedimento.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_PROCEDIMENTO_ESPECIAL", procedimento.getDescricao());
			}
			
			limparObjetos();
			
		} catch (BaseException e) {
			limparFilhos(criando, modosUsosAux, modosUsosExcAux, materiaisMpmProcedEspecialRmAux, materiaisMpmProcedEspecialRmExcluidosAux);
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseRuntimeException e) {
			limparFilhos(criando, modosUsosAux, modosUsosExcAux, materiaisMpmProcedEspecialRmAux, materiaisMpmProcedEspecialRmExcluidosAux);
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_PESQUISA_PROCEDIMENTOS_ESPECIAIS;
	}
	
	private void clonarListas(  List<MpmTipoModoUsoProcedimento> modosUsosAux,
								List<MpmTipoModoUsoProcedimento> modosUsosExcAux,
								List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmAux,
								List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidosAux) throws ApplicationBusinessException {
		try {

			for (MpmProcedEspecialRm ahClonar : materiaisMpmProcedEspecialRmExcluidos) {
				materiaisMpmProcedEspecialRmExcluidosAux.add((MpmProcedEspecialRm) BeanUtils.cloneBean(ahClonar));
			}
			
			for (MpmProcedEspecialRm ahClonar : materiaisMpmProcedEspecialRm) {
				materiaisMpmProcedEspecialRmAux.add((MpmProcedEspecialRm) BeanUtils.cloneBean(ahClonar));
			}
			
			for (MpmTipoModoUsoProcedimento ahClonar : modosUsosExcluidos) {
				modosUsosExcAux.add((MpmTipoModoUsoProcedimento) BeanUtils.cloneBean(ahClonar));
			}

			for (MpmTipoModoUsoProcedimento ahClonar : modosUsos) {
				modosUsosAux.add((MpmTipoModoUsoProcedimento) BeanUtils.cloneBean(ahClonar));
			}
			
		} catch (IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {
	    	throw new ApplicationBusinessException(ProcedimentosEspeciaisControllerExceptionCode.ERRO_ATUALIZAR_PROCEDIMENTO_ESPECIAL);
		}
	}

	public void limparFilhos(boolean isInsert, List<MpmTipoModoUsoProcedimento> modosUsosAux, List<MpmTipoModoUsoProcedimento> modosUsosExcAux,
			List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmAux, List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidosAux){
		
		if (isInsert) {
			procedimento.setSeq(null);
		}
		
		modosUsos = modosUsosAux;
		modosUsosExcluidos = modosUsosExcAux;
		
		materiaisMpmProcedEspecialRm = materiaisMpmProcedEspecialRmAux;
		materiaisMpmProcedEspecialRmExcluidos = materiaisMpmProcedEspecialRmExcluidosAux;
	}

	/*public String cancelarProcedimento() {
		limparObjetos();
		return PAGE_PESQUISA_PROCEDIMENTOS_ESPECIAIS;
	}*/
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de procedimentos especiais
	 */
	public String cancelarProcedimento() {
		if(!listaFatProcedimentoRelacionadosVO.isEmpty() && !chamarCancelar){
			setChamarCancelar(Boolean.TRUE);
			return "";
		} else {
			this.limparObjetos();
		}	
		return PAGE_PESQUISA_PROCEDIMENTOS_ESPECIAIS;
	}

	private void limparObjetos() {
		
		setChamarCancelar(Boolean.FALSE);
		prescricaoMedicaFacade.desatacharMpmProcedEspecialDiversos(procedimento);
		procedimento = new MpmProcedEspecialDiversos();
		modoUso = new MpmTipoModoUsoProcedimento();
		materialRmAutomatica = new MpmProcedEspecialRm();
		modoUsoEmEdicaoId = null;
		materialRMAutomaticaEmEdicaoId = null;
		codigo = null;
	}

	public void limpar() {
		limparObjetos();
		material = null;
	} 

	public void adicionarItemModoUso() {

		// Realiza validação de campos obrigatórios
		if (StringUtils.isBlank(modoUso.getDescricao())) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ProcedimentosEspeciaisControllerExceptionCode.ERRO_DESCRICAO_MODO_USO_OBRIGATORIO));
			return;
		}
		
		if (modoUso.getIndSituacao() == null) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ProcedimentosEspeciaisControllerExceptionCode.ERRO_SITUACAO_MODO_USO_OBRIGATORIO));
			return;
		}

		// Adicionar id temporário para referência na tela apenas
		MpmTipoModoUsoProcedimentoId tempId = new MpmTipoModoUsoProcedimentoId();
		if (procedimento.getSeq() != null) {
			tempId.setPedSeq(procedimento.getSeq());
		} else {
			tempId.setPedSeq(ID_PROCEDIMENTO_NAO_SALVO);
		}
		tempId.setSeqp(++maiorSeqModoUso);
		modoUso.setId(tempId);
		modoUso.setProcedimentoEspecialDiverso(procedimento);
		modosUsos.add(modoUso);
		modoUso = new MpmTipoModoUsoProcedimento();
	}


	public void ativarInativarModoUso(MpmTipoModoUsoProcedimento elemento) {
		if(DominioSituacao.A.equals(elemento.getIndSituacao())){
			elemento.setIndSituacao(DominioSituacao.I);
		} else {
			elemento.setIndSituacao(DominioSituacao.A);
		}
	}
	
	public void atualizarItemModoUso() {
		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());
		} catch (BaseException e) {
			//LOG.debug("Erro ao recuperar o servidor, exceção ignorada.");
			apresentarExcecaoNegocio(e);
		}
		
		for (MpmTipoModoUsoProcedimento modoUsoOld : procedimento
				.getModosProcedimentosPrescricaoDeProcedimentos()) {
			if (modoUso.getId().getSeqp() == modoUsoOld.getId().getSeqp()) {
				modoUsoOld.setIndSituacao(modoUso.getIndSituacao());
				modoUsoOld.setServidor(servidorLogado);
				break;
			}
		}
		this.cancelarEdicaoItemModoUso();
	}
	
	/**
	 * Método que cancela o processo de edicao de um modo de uso
	 */
	public void cancelarEdicaoItemModoUso() {
		modoUso = new MpmTipoModoUsoProcedimento();
		modoUsoEmEdicaoId = null;
		emEdicaoModoUso = false;
	}
	
	/**
	 * Método que realiza a ação do botão editar da tabela de modos de uso
	 */
	public void iniciarEdicaoModoUso(MpmTipoModoUsoProcedimento elemento) {
		modoUsoEmEdicaoId = elemento.getId().getSeqp();
		modoUso = new MpmTipoModoUsoProcedimento(elemento.getId(), elemento.getProcedimentoEspecialDiverso(), elemento.getServidor(), elemento.getDescricao(),
												 elemento.getIndExigeQuantidade(), elemento.getCriadoEm(), elemento.getIndSituacao());
		emEdicaoModoUso = true;
	}
	
	/**
	 * Método que recupera os modos de uso de um procedimento para a tela de cadastro de procedimentos especiais
	 * (inclusive não salvos)
	 */
	public List<MpmTipoModoUsoProcedimento> getModosUso() {
		 Set<MpmTipoModoUsoProcedimento> modosUso = procedimento.getModosProcedimentosPrescricaoDeProcedimentos();
		 if(modosUso == null || modosUso.isEmpty()) {
			 return new LinkedList<MpmTipoModoUsoProcedimento>();
		 } else {
			 List<MpmTipoModoUsoProcedimento> modosUsoList = new ArrayList<MpmTipoModoUsoProcedimento>(modosUso);
			 Collections.sort(modosUsoList, new Comparator<MpmTipoModoUsoProcedimento>() {
				 @Override
				 public int compare(MpmTipoModoUsoProcedimento modosUso1, MpmTipoModoUsoProcedimento modosUso2) {
					 return modosUso1.getDescricao().compareTo(modosUso2.getDescricao());
				 }
			 });
			 return modosUsoList;
		 }
	}
	
	/**
	 * Método que verifica se determinado modo de uso é o que está em edição no momento
	 */
	public boolean modoUsoEmEdicao(MpmTipoModoUsoProcedimento elemento) {
		if(modoUsoEmEdicaoId != null && elemento.getId() != null) {
			return modoUsoEmEdicaoId.equals(elemento.getId().getSeqp());
		} else {
			return false;
		}
	}

	public void excluirModoUso(MpmTipoModoUsoProcedimento elemento) {
		// Valida se o modo de uso não está sendo usado
		boolean modoUsoEmUsoPrescricao = prescricaoMedicaFacade.verificaModoUsoProcedimentoEspecialPrescrito(elemento.getId().getPedSeq(),elemento.getId().getSeqp());
		boolean modoUsoEmUsoModeloBasico = prescricaoMedicaFacade.verificaModoUsoEmModeloBasico(elemento.getId().getPedSeq(),elemento.getId().getSeqp());
		boolean modoUsoEmUso = modoUsoEmUsoPrescricao || modoUsoEmUsoModeloBasico;

		if (modoUsoEmUso) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_REMOCAO_MODO_USO_EM_USO", modoUso.getDescricao());
		} else {
			modosUsosExcluidos.add(elemento);
			modosUsos.remove(elemento);
		}
	}

	public void adicionarItemMaterialRMAutomatica() {
		if(material == null) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ProcedimentosEspeciaisControllerExceptionCode.ERRO_MATERIAL_MATERIAIS_RM_AUTOMATICA_OBRIGATORIO));
			return;
		}
		if(materialRmAutomatica.getSituacao() == null) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ProcedimentosEspeciaisControllerExceptionCode.ERRO_SITUACAO_MATERIAIS_RM_AUTOMATICA_OBRIGATORIO));
			return;
		}
		
		materialRmAutomatica.setMaterial(material);
		
		final MpmProcedEspecialRmId id = new MpmProcedEspecialRmId();
		id.setMatCodigo(material.getCodigo());
		if (procedimento.getSeq() != null) {
			id.setPedSeq(procedimento.getSeq());
		} else {
			id.setPedSeq(ID_PROCEDIMENTO_NAO_SALVO);
		}
		materialRmAutomatica.setId(id);

		// Verifica se já não existe o material na lista
		if (materiaisMpmProcedEspecialRm.contains(materialRmAutomatica)) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_MATERIAL_JA_EXISTE", procedimento.getDescricao());
			
		} else {
			materialRmAutomatica.setProcedimentoEspecialDiverso(procedimento);

			materiaisMpmProcedEspecialRm.add(materialRmAutomatica);
			materialRmAutomatica = new MpmProcedEspecialRm();
			material = null;
		}
	}

	public void ativarInativarMaterialRMAutomatica(MpmProcedEspecialRm elemento) {
		if(DominioSituacao.A.equals(elemento.getSituacao())){
			elemento.setSituacao(DominioSituacao.I);
		} else {
			elemento.setSituacao(DominioSituacao.A);
		}
	}
	
	/**
	 * Metódo para SuggestionBox
	 *  
	 */
	public List<ScoMaterial> obterMateriaisSuggestionBox(String nome) throws ApplicationBusinessException{
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRPO_MAT_MEDICO_HOSP);

		Integer gmtCodigo = null;
		if(parametro != null) {
			gmtCodigo = parametro.getVlrNumerico().intValue();
		}
		
		return cadastrosBasicosPrescricaoMedicaFacade.listarMateriaisRMAutomatica(gmtCodigo, (String) nome);
	}

	public void excluirMaterialRMAutomatica(MpmProcedEspecialRm elemento) {
		materiaisMpmProcedEspecialRm.remove(elemento);
		materiaisMpmProcedEspecialRmExcluidos.add(elemento);
	}
	
	/**
	 * Método que verifica se determinado material é o que está em edição no momento
	 */
	public boolean materialRMAutomaticaEmEdicao(MpmProcedEspecialRm elemento) {
		if(materialRMAutomaticaEmEdicaoId != null && elemento.getId() != null) {
			return materialRMAutomaticaEmEdicaoId.equals(elemento.getId().getMatCodigo());
		} else {
			return false;
		}
	}
	
	public void cancelarProcedimentoRelacionado(){
		this.editandoProcedimentosRelacionados = Boolean.FALSE;
	}
	
	public void editarProcedimentoRelacionado(VFatConvPlanoGrupoProcedVO vo){
		this.editandoProcedimentosRelacionados = Boolean.TRUE;
		this.setvFatConvPlanoGrupoProcedVO(vo);
		this.fatItensProcedHospitalar = vo.getFatItensProcedHospitalar();
	}
	
	public void alterarProcedimentoRelacionado(){
		this.editandoProcedimentosRelacionados = Boolean.FALSE;
		if(fatItensProcedHospitalar != null){
			vFatConvPlanoGrupoProcedVO.setFatItensProcedHospitalar(fatItensProcedHospitalar);
		}	
	}
	
	//------------------------------------------------------
		//Métodos para manter Materiais
		
		/**
		 * Método que recupera os materiais de um procedimento para a tela de cadastro de procedimentos especiais
		 * (inclusive não salvos)
		 */
		public List<MpmProcedEspecialRm> getMateriaisRMAutomatica() {
			 Set<MpmProcedEspecialRm> materiais = procedimento.getMateriaisDaRequisicaoPrescricaoDeProcedimentos();
			 if(materiais == null || materiais.isEmpty()) {
				 return new LinkedList<MpmProcedEspecialRm>();
			 } else {
				 List<MpmProcedEspecialRm> materiaisList = new ArrayList<MpmProcedEspecialRm>(materiais);
				 Collections.sort(materiaisList, new Comparator<MpmProcedEspecialRm>() {
					 @Override
					 public int compare(MpmProcedEspecialRm material1, MpmProcedEspecialRm material2) {
						 if(material1.getMaterial().getNome() == null && material2.getMaterial().getNome() != null) {
							 return 1;
						 }
						 if(material1.getMaterial().getNome() != null && material2.getMaterial().getNome() == null) {
							 return -1;
						 }
						 if(material1.getMaterial().getNome() == null && material2.getMaterial().getNome() == null) {
							 return 0;
						 }
						 return material1.getMaterial().getNome().compareTo(material2.getMaterial().getNome());
					 }
				 });
				 return materiaisList;
			 }
		}
		
		/**
		 * Método que atualiza os dados de um material editado
		 */
		public void atualizarItemMaterialRMAutomatica() {
			for(MpmProcedEspecialRm materialOld : procedimento.getMateriaisDaRequisicaoPrescricaoDeProcedimentos()) {
				if(material != null && material.getCodigo().equals(materialOld.getId().getMatCodigo())) {
					materialOld.setSituacao(materialRmAutomatica.getSituacao());
					break;
				}
			}
			materialRmAutomatica = new MpmProcedEspecialRm();
			materialRMAutomaticaEmEdicaoId = null;
			material = null;
			emEdicaoMaterialRMAutomatica = false;
		}
		
		/**
		 * Método que cancela o processo de edicao de um material
		 */
		public void cancelarEdicaoItemMaterialRMAutomatica() {
			materialRmAutomatica = new MpmProcedEspecialRm();
			materialRMAutomaticaEmEdicaoId = null;
			emEdicaoMaterialRMAutomatica = false;
		}
		
		/**
		 * Método que realiza a ação do botão editar da tabela de materias
		 */
		public void iniciarEdicaoMaterialRMAutomatica(MpmProcedEspecialRm elemento) {
			materialRMAutomaticaEmEdicaoId = elemento.getId().getMatCodigo();
			material = elemento.getMaterial();
			materialRmAutomatica = new MpmProcedEspecialRm(elemento.getId(), elemento.getProcedimentoEspecialDiverso(), elemento.getCriadoEm(), elemento.getServidor(), elemento.getSituacao());
			materialRmAutomatica.setMaterial(elemento.getMaterial());
			emEdicaoMaterialRMAutomatica = true;
		}
	
	public Boolean getChamarCancelar() {
		return chamarCancelar;
	}

	public void setChamarCancelar(Boolean chamarCancelar) {
		this.chamarCancelar = chamarCancelar;
	}
 
	public MpmProcedEspecialDiversos getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(MpmProcedEspecialDiversos procedimento) {
		this.procedimento = procedimento;
	}

	public MpmTipoModoUsoProcedimento getModoUso() {
		return modoUso;
	}

	public void setModoUso(MpmTipoModoUsoProcedimento modoUso) {
		this.modoUso = modoUso;
	}

	public MpmProcedEspecialRm getMaterialRmAutomatica() {
		return materialRmAutomatica;
	}

	public void setMaterialRmAutomatica(MpmProcedEspecialRm materialRmAutomatica) {
		this.materialRmAutomatica = materialRmAutomatica;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public boolean isEmEdicaoModoUso() {
		return emEdicaoModoUso;
	}

	public void setEmEdicaoModoUso(boolean emEdicaoModoUso) {
		this.emEdicaoModoUso = emEdicaoModoUso;
	}
	
	public boolean isEmEdicaoMaterialRMAutomatica() {
		return emEdicaoMaterialRMAutomatica;
	}
	
	public void setEmEdicaoMaterialRMAutomatica(boolean emEdicaoMaterialRMAutomatica) {
		this.emEdicaoMaterialRMAutomatica = emEdicaoMaterialRMAutomatica;
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public List<MpmProcedEspecialRm> getMateriaisMpmProcedEspecialRm() {
		return materiaisMpmProcedEspecialRm;
	}

	public void setMateriaisMpmProcedEspecialRm(List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm) {
		this.materiaisMpmProcedEspecialRm = materiaisMpmProcedEspecialRm;
	}
	
	public List<MpmTipoModoUsoProcedimento> getModosUsos() {
		return modosUsos;
	}

	public void setModosUsos(List<MpmTipoModoUsoProcedimento> modosUsos) {
		this.modosUsos = modosUsos;
	}

	public List<MpmTipoModoUsoProcedimento> getModosUsosExcluidos() {
		return modosUsosExcluidos;
	}

	public void setModosUsosExcluidos(
			List<MpmTipoModoUsoProcedimento> modosUsosExcluidos) {
		this.modosUsosExcluidos = modosUsosExcluidos;
	}

	public List<MpmProcedEspecialRm> getMateriaisMpmProcedEspecialRmExcluidos() {
		return materiaisMpmProcedEspecialRmExcluidos;
	}

	public void setMateriaisMpmProcedEspecialRmExcluidos(
			List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidos) {
		this.materiaisMpmProcedEspecialRmExcluidos = materiaisMpmProcedEspecialRmExcluidos;
	}
	
	public List<VFatConvPlanoGrupoProcedVO> getListaFatProcedimentoRelacionadosVO() {
		return listaFatProcedimentoRelacionadosVO;
	}

	public void setListaFatProcedimentoRelacionadosVO(List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO) {
		this.listaFatProcedimentoRelacionadosVO = listaFatProcedimentoRelacionadosVO;
	}
	
	// -----------
	// -- 34742 --
	// -----------	

	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(Object objPesquisa) {
		return this.faturamentoFacade.listarConvenios(objPesquisa, getCpgGrcSeq(), getTabela().getCphPhoSeq());
	}
	
	public Long listarConveniosCount(Object objPesquisa) {
		return this.faturamentoFacade.listarConveniosCount(objPesquisa, getCpgGrcSeq(), getTabela().getCphPhoSeq());
	}
	
	public List<FatItensProcedHospitalar>  listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(Object objPesquisa){
		return this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(objPesquisa, this.tabela.getCphPhoSeq());
	}
	
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(Object objPesquisa){
		return this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, this.tabela.getCphPhoSeq());
	}
	
	public void excluirProcedimentoRelacionado(Integer index){
		this.listaFatProcedimentoRelacionadosVO.remove(index.intValue());
	}
	
	public void adicionarProcedimentosRelacionados(){
		if(fatItensProcedHospitalar != null && convenio != null){
			try {
				cadastrosApoioExamesFacade.adicionarProcedimentosRelacionados(getListaFatProcedimentoRelacionadosVO(), convenio, fatItensProcedHospitalar, this.cpgGrcSeq);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}	
//		this.pesquisarProcedimentosInternosPeloSeqProcCirg();
	}
	
	public VFatConvPlanoGrupoProcedVO getvFatConvPlanoGrupoProcedVO() {
		return vFatConvPlanoGrupoProcedVO;
	}

	public void setvFatConvPlanoGrupoProcedVO(VFatConvPlanoGrupoProcedVO vFatConvPlanoGrupoProcedVO) {
		this.vFatConvPlanoGrupoProcedVO = vFatConvPlanoGrupoProcedVO;
	}
	
	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}

	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}
	
	public VFatConvPlanoGrupoProcedVO getTabela() {
		return tabela;
	}

	public void setTabela(VFatConvPlanoGrupoProcedVO tabela) {
		this.tabela = tabela;
	}
	
	public Short getCpgCphPhoSeq() {
		return cpgCphPhoSeq;
	}

	public void setCpgCphPhoSeq(Short cpgCphPhoSeq) {
		this.cpgCphPhoSeq = cpgCphPhoSeq;
	}
	
	public List<FatProcedHospInternos> getListaFatProcedHospInternos() {
		return listaFatProcedHospInternos;
	}

	public void setListaFatProcedHospInternos(List<FatProcedHospInternos> listaFatProcedHospInternos) {
		this.listaFatProcedHospInternos = listaFatProcedHospInternos;
	}
	
	public void setEditandoProcedimentosRelacionados(Boolean editandoProcedimentosRelacionados) {
		this.editandoProcedimentosRelacionados = editandoProcedimentosRelacionados;
	}

	public Boolean getEditandoProcedimentosRelacionados() {
		return editandoProcedimentosRelacionados;
	}
	
	public VFatConvPlanoGrupoProcedVO getEditarFatProcedimentoRelacionadosVO() {
		return editarFatProcedimentoRelacionadosVO;
	}

	public void setEditarFatProcedimentoRelacionadosVO(
			VFatConvPlanoGrupoProcedVO editarFatProcedimentoRelacionadosVO) {
		this.editarFatProcedimentoRelacionadosVO = editarFatProcedimentoRelacionadosVO;
	}

	public Boolean getTemPerfilOrdenadorDespesas() {
		return temPerfilOrdenadorDespesas;
	}

	public void setTemPerfilOrdenadorDespesas(Boolean temPerfilOrdenadorDespesas) {
		this.temPerfilOrdenadorDespesas = temPerfilOrdenadorDespesas;
	}

	public Boolean getChamarConfirmarSemVinculos() {
		return chamarConfirmarSemVinculos;
	}

	public void setChamarConfirmarSemVinculos(Boolean chamarConfirmarSemVinculos) {
		this.chamarConfirmarSemVinculos = chamarConfirmarSemVinculos;
	}

	public RelacionarPHISSMController getRelacionarPHISSMController() {
		return relacionarPHISSMController;
	}

	public void setRelacionarPHISSMController(
			RelacionarPHISSMController relacionarPHISSMController) {
		this.relacionarPHISSMController = relacionarPHISSMController;
	}

	public Short getModoUsoEmEdicaoId() {
		return modoUsoEmEdicaoId;
	}

	public void setModoUsoEmEdicaoId(Short modoUsoEmEdicaoId) {
		this.modoUsoEmEdicaoId = modoUsoEmEdicaoId;
	}

	public Integer getMaterialRMAutomaticaEmEdicaoId() {
		return materialRMAutomaticaEmEdicaoId;
	}

	public void setMaterialRMAutomaticaEmEdicaoId(
			Integer materialRMAutomaticaEmEdicaoId) {
		this.materialRMAutomaticaEmEdicaoId = materialRMAutomaticaEmEdicaoId;
	}

	public Short getMaiorSeqModoUso() {
		return maiorSeqModoUso;
	}

	public void setMaiorSeqModoUso(Short maiorSeqModoUso) {
		this.maiorSeqModoUso = maiorSeqModoUso;
	}

	public FatItensProcedHospitalar getFatItensProcedHospitalar() {
		return fatItensProcedHospitalar;
	}

	public void setFatItensProcedHospitalar(
			FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
	}

	public Boolean getExibirPainelInferior() {
		return exibirPainelInferior;
	}

	public void setExibirPainelInferior(Boolean exibirPainelInferior) {
		this.exibirPainelInferior = exibirPainelInferior;
	}

	public Boolean getAlterouItensProcedimento() {
		return alterouItensProcedimento;
	}

	public void setAlterouItensProcedimento(Boolean alterouItensProcedimento) {
		this.alterouItensProcedimento = alterouItensProcedimento;
	}

	public VFatConvPlanoGrupoProcedVO getConvenio() {
		return convenio;
	}

	public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
		this.convenio = convenio;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedList() {
		return convGrupoItemProcedList;
	}

	public void setConvGrupoItemProcedList(
			List<FatConvGrupoItemProced> convGrupoItemProcedList) {
		this.convGrupoItemProcedList = convGrupoItemProcedList;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedListOriginal() {
		return convGrupoItemProcedListOriginal;
	}

	public void setConvGrupoItemProcedListOriginal(
			List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal) {
		this.convGrupoItemProcedListOriginal = convGrupoItemProcedListOriginal;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedListRemover() {
		return convGrupoItemProcedListRemover;
	}

	public void setConvGrupoItemProcedListRemover(
			List<FatConvGrupoItemProced> convGrupoItemProcedListRemover) {
		this.convGrupoItemProcedListRemover = convGrupoItemProcedListRemover;
	}

	public Integer getPhi() {
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public FatConvenioSaudePlano getPlano() {
		return plano;
	}

	public void setPlano(FatConvenioSaudePlano plano) {
		this.plano = plano;
	}

	public FatConvGrupoItemProced getFatConvGrupoItemProcedSelecionado() {
		return fatConvGrupoItemProcedSelecionado;
	}

	public void setFatConvGrupoItemProcedSelecionado(
			FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado) {
		this.fatConvGrupoItemProcedSelecionado = fatConvGrupoItemProcedSelecionado;
	}

	public boolean isObrigatoriedadeSigtap() {
		return obrigatoriedadeSigtap;
	}

	public void setObrigatoriedadeSigtap(boolean obrigatoriedadeSigtap) {
		this.obrigatoriedadeSigtap = obrigatoriedadeSigtap;
	}
	
}