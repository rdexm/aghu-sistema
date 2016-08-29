package br.gov.mec.aghu.prescricaomedica.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.internacao.vo.JustificativaComponenteSanguineoVO;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativaId;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicasId;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.CompSanguineoProcedHemoterapicoVO;
import br.gov.mec.aghu.prescricaomedica.vo.CompSanguineoProcedHemoterapicoVO.DominioTipo;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class ManterSolicitacaoHemoterapiaController extends ActionController {

	
	private static final String JA_ADICIONADO_A_SOLICITACAO = " já adicionado a solicitação.";
	private static final String ITEM_HEMOTERAPICO = "Item hemoterapico ";
	private static final String EXCECAO_CAPUTADA = "Exceção caputada:";
	private static final long serialVersionUID = 6227375010573377684L;
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final Log LOG = LogFactory.getLog(ManterSolicitacaoHemoterapiaController.class);

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	

	@EJB
	private IPacienteFacade pacienteFacade;

	private PrescricaoMedicaVO prescricaoMedicaVO;

	private boolean confirmaVoltar;

	private String codigoItem;
	
	private AbsSolicitacoesHemoterapicasId idSolicitacao;
	
	private Byte quantidadeUnidade;
	
	private Short quantidadeMl;
	
	private Short quantidadeAplicacoes;
	
	private CompSanguineoProcedHemoterapicoVO compSanguineoProcedHemoterapicoVO;
	
	private Boolean indIrradiado = false;
	
	private Boolean indDesleucocitado = false;
	
	private Boolean indLavado = false;
	
	private Boolean indAferese = false;
	
	private Boolean indTransfusoes = false;
	
	private Boolean indTransplantado = false;
	
	private Boolean indUrgente = false;
	
	private MpmTipoFrequenciaAprazamento tipoAprazamento;
	
	private Short frequencia;
	
	//private Boolean exibirModal;

	//private String mensagemExibicaoModal;
	
	private String observacao;
	
	private int indice = 0;
	
	private Long seq;
	
	private Boolean edicao = false;
	
	private Boolean edicaoItemHemoterapico = false;
	
	private AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = null; 
	
	private List<AbsSolicitacoesHemoterapicas> listaSolicitacoesHemoterapias;

	private Map<AbsSolicitacoesHemoterapicas, Boolean> listaSolicitacoesHemoterapiaSelecionas = new HashMap<AbsSolicitacoesHemoterapicas, Boolean>();
	
	private AbsSolicitacoesHemoterapicasId solicitacaoHemoterapicaId;
	
	private List<AbsItensSolHemoterapicas> listaItensHemoterapicos = new ArrayList<AbsItensSolHemoterapicas>();
	
	private AbsItensSolHemoterapicasId itemId;
	
	private Integer atdSeq;
	
	private	Integer seqShe;
	
	private Short seqItem;
	
	private String codigoComposto;

	//Usado na modal de justificativas
	private List<AbsGrupoJustificativaComponenteSanguineo> listaGruposDeJustificativas;

	//Usado na modal de justificativas
	private String descricaoJustificativaSolicitacao;
	private String procedHemoterapicoCodigo; 
	private String componenteSanguineoCodigo;

	private Map<Short, List<JustificativaComponenteSanguineoVO>> justificativasSelecionadas;
	
	private List<String> exames;
	
	private int idConversacaoAnterior;
	
	private int selectedTab = 0;
	
	private boolean itemFormModified;

	private AbsItensSolHemoterapicas item2Edit;
	
	private boolean confirmaEdicaoItemPendente;

	private AbsSolicitacoesHemoterapicas solicitacao2Edit;
	
	private boolean confirmaEdicaoPendente;
	
	private boolean formModified;
	
	private AbsItensSolHemoterapicas itensSolHemoterapicas;
	
	
	private enum ManterSolicitacaoHemoterapiaControllerExceptionCode implements
			BusinessExceptionCode {
		SUCESSO_GRAVAR_SOLICITACAO_HEMOTERAPICA_JUSTIFICATIVA, IMPOSSIVEL_EXCLUIR_APENAS_UM_ITEM_CADASTRADO,
		ITENS_SELECIONADOS_NAO_PERMITIDOS_PARA_PROCEDIMENTO,ABS_00829, ABS_00384, SOLICITACAO_HEMOTERAPICA_INSERIDA_SUCESSO,
		SOLICITACAO_HEMOTERAPICA_ALTERADA_SUCESSO, LISTA_ITENS_HEMOTERAPICOS_VAZIA, MPM_03353, OBSERVACAO_HEMO_INVALIDA, JUSTIFICATIVA_HEMO_INVALIDA
	}

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
	 

		this.setEdicaoItemHemoterapico(false);
		this.confirmaVoltar = false;
		
		this.limpar();
		
		//this.exibirModal = false;
		
		if (getPrescricaoMedicaVO() != null) {
			this.listaSolicitacoesHemoterapias = prescricaoMedicaFacade
					.obterListaSolicitacoesHemoterapicasPelaChavePrescricao(new MpmPrescricaoMedicaId(getPrescricaoMedicaVO().getPrescricaoMedica().getId().getAtdSeq(), getPrescricaoMedicaVO().getPrescricaoMedica().getId().getSeq()));
			for (AbsSolicitacoesHemoterapicas solHemoterapica : listaSolicitacoesHemoterapias) {
				listaSolicitacoesHemoterapiaSelecionas.put(solHemoterapica, false);
			}
									
			final BeanComparator descricaoSorter = new BeanComparator("descricaoFormatada", new NullComparator(false));
			if(listaSolicitacoesHemoterapias != null && !listaSolicitacoesHemoterapias.isEmpty()) {
				Collections.sort(listaSolicitacoesHemoterapias, descricaoSorter);
			}
			
			this.listaItensHemoterapicos = new ArrayList<AbsItensSolHemoterapicas>();
		} else {
			listaSolicitacoesHemoterapias = new ArrayList<AbsSolicitacoesHemoterapicas>();
			listaSolicitacoesHemoterapiaSelecionas = new HashMap< AbsSolicitacoesHemoterapicas, Boolean >();
			listaItensHemoterapicos= new ArrayList<AbsItensSolHemoterapicas>();
		}

		if (this.solicitacaoHemoterapica != null && this.solicitacaoHemoterapica.getId() != null && this.solicitacaoHemoterapica.getId().getSeq() != null) {
			this.editar(solicitacaoHemoterapica);
		} else {
			this.solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		}
		
		if (this.seq != null) {
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(getPrescricaoMedicaVO().getId().getAtdSeq(), seq.intValue());
			if (solicitacaoHemoterapica != null) {
				this.editar(solicitacaoHemoterapica);
			} else {
				//controle caso o item tenha sido excluído por outro usuário
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			}
		}
	}
	
	public void verificaAferese() throws ApplicationBusinessException{
		if (this.compSanguineoProcedHemoterapicoVO.getAvisoAferese() != null
				&& this.indAferese) {
			apresentarMsgNegocio(Severity.INFO,
					this.compSanguineoProcedHemoterapicoVO.getAvisoAferese());
		}
		this.setItemFormModified(true);
	}
	
	public void changeTipoItem(AjaxBehaviorEvent event)
			throws ApplicationBusinessException {		
		
		this.quantidadeMl=null;
		this.quantidadeUnidade=null;
		this.indAferese=false;
		this.indLavado=false;
		this.indIrradiado=false;
		this.indDesleucocitado=false;
		
		if (this.codigoComposto != null) {
			for (CompSanguineoProcedHemoterapicoVO tipoItem : getListaItens()) {
				if (tipoItem.getCodigoComposto().equals(this.codigoComposto)) {
					this.compSanguineoProcedHemoterapicoVO = tipoItem;
				}
			}
		} else{
			this.compSanguineoProcedHemoterapicoVO = null;
			this.codigoComposto = null;
		}
		
		if (this.compSanguineoProcedHemoterapicoVO != null && this.compSanguineoProcedHemoterapicoVO.getAvisoPrescricao()!=null){
			apresentarMsgNegocio(Severity.INFO,this.compSanguineoProcedHemoterapicoVO.getAvisoPrescricao());
		}
		
		this.setItemFormModified(true);
	}
	
	/**
	 * Limpa os campos da tela.
	 */
	public String limpar() {
		limpaItemForm();
		this.setFormModified(false);
		this.setItemFormModified(false);
		this.setConfirmaEdicaoPendente(false);
		this.setConfirmaEdicaoItemPendente(false);
		
		this.confirmaVoltar = false;
		this.edicaoItemHemoterapico = false;
		this.edicao = false;
		
		this.idSolicitacao = null;
		this.observacao = null;
		this.indTransfusoes = false;
		this.indTransplantado = false;
		this.indUrgente = false;
		this.solicitacaoHemoterapica = null;
		this.listaItensHemoterapicos = new ArrayList<AbsItensSolHemoterapicas>();

		this.solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		return "manterSolicitacaoHemoterapia";
	}	
	
	public void  removerSolicitacoesHemoterapiaSelecionadas() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPUTADA, e);
		}
		
		try {
			int nroSolicitacoesHemoterapiaRemovidas = 0;
			for (AbsSolicitacoesHemoterapicas solHemoterapia: listaSolicitacoesHemoterapias) {
				if (listaSolicitacoesHemoterapiaSelecionas.get(solHemoterapia) == true) {
					this.prescricaoMedicaFacade.excluirSolicitacaoHemoterapica(solHemoterapia, nomeMicrocomputador);
					nroSolicitacoesHemoterapiaRemovidas++;
				}
			}
			if (nroSolicitacoesHemoterapiaRemovidas > 0) {
				if (nroSolicitacoesHemoterapiaRemovidas > 1) {
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_SOLICITACOES_HEMOTERAPICAS");
				} else {
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_SOLICITACAO_HEMOTERAPICA");
				}
			} else{
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_NENHUMA_SOLICITACAO_HEMOTERAPICA_SELECIONADA_REMOCAO");
			}
			//Limpa a tela
			this.limpar();
			this.listaSolicitacoesHemoterapias = prescricaoMedicaFacade
				.obterListaSolicitacoesHemoterapicasPelaChavePrescricao(
						new MpmPrescricaoMedicaId(getPrescricaoMedicaVO().getPrescricaoMedica().getId().getAtdSeq(), 
								getPrescricaoMedicaVO().getPrescricaoMedica().getId().getSeq()));			
			for (AbsSolicitacoesHemoterapicas solHemoterapia: listaSolicitacoesHemoterapias) {
				listaSolicitacoesHemoterapiaSelecionas.put(solHemoterapia, false);
			}			
						
			final BeanComparator descricaoSorter = new BeanComparator("descricaoFormatada", new NullComparator(false));
			if (listaSolicitacoesHemoterapias != null && !listaSolicitacoesHemoterapias.isEmpty()) {
				Collections.sort(listaSolicitacoesHemoterapias, descricaoSorter);
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editar(AbsSolicitacoesHemoterapicas solicitacaoHemoterapica) {
		if (this.isFormModified()) {
			solicitacao2Edit = solicitacaoHemoterapica;
			this.setConfirmaEdicaoPendente(true);
			return;
		}
		
		this.limpar();
		
		this.edicaoItemHemoterapico = true;
		this.edicao = false;
		this.idSolicitacao = new AbsSolicitacoesHemoterapicasId(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq());
		
		if (DominioIndPendenteItemPrescricao.N.equals(solicitacaoHemoterapica.getIndPendente())) {
			try {
				this.solicitacaoHemoterapica = prescricaoMedicaFacade.clonarSolicitacaoHemoterapica(bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq()));
			} catch(ApplicationBusinessException e) {
				this.solicitacaoHemoterapica = this.bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq()); 
			} 
		} else {
			this.solicitacaoHemoterapica = this.bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq()); 
		}			
		
		this.listaItensHemoterapicos = new ArrayList<AbsItensSolHemoterapicas>(this.solicitacaoHemoterapica.getItensSolHemoterapicas());
		this.observacao = this.solicitacaoHemoterapica.getObservacao();
		this.indUrgente = this.solicitacaoHemoterapica.getIndUrgente();
		this.indTransfusoes = this.solicitacaoHemoterapica.getIndTransfAnteriores();
		this.indTransplantado = this.solicitacaoHemoterapica.getIndPacTransplantado();
	}
	
	public void confirmaEditar() {
		this.setFormModified(false);
		this.setItemFormModified(false);
		this.editar(solicitacao2Edit);
		this.setConfirmaEdicaoPendente(false);
		solicitacao2Edit = null;
	}
	
	public void editarItemHemoterapico(AbsItensSolHemoterapicas item) {
		if (this.isItemFormModified()) {
			item2Edit = item;
			this.setConfirmaEdicaoItemPendente(true);
			return;
		}
		
		this.setEdicao(true);
		this.indice = 0;
		this.confirmaVoltar = false;
		
		for (AbsItensSolHemoterapicas itemHem : listaItensHemoterapicos) {
			this.indice++;

			if ((itemHem.getComponenteSanguineo() != null
					&& item.getComponenteSanguineo() != null && itemHem
					.getComponenteSanguineo().getCodigo().equals(
							item.getComponenteSanguineo().getCodigo()))
					|| (itemHem.getProcedHemoterapico() != null
							&& item.getProcedHemoterapico() != null && itemHem
							.getProcedHemoterapico().getCodigo().equals(
									item.getProcedHemoterapico().getCodigo()))) {

				if(itemHem.getComponenteSanguineo() != null) {
					codigoItem = itemHem.getComponenteSanguineo().getCodigo();
				} else if(itemHem.getProcedHemoterapico() != null) {
					codigoItem = itemHem.getProcedHemoterapico().getCodigo();
				}
				
				this.itemId = item.getId();								
				this.indAferese = item.getIndAferese();
				this.indDesleucocitado = item.getIndFiltrado();
				this.indIrradiado = item.getIndIrradiado();
				this.indLavado = item.getIndLavado();
				this.quantidadeMl = item.getQtdeMl();
				this.quantidadeUnidade = item.getQtdeUnidades();
				this.quantidadeAplicacoes = item.getQtdeAplicacoes();
				this.frequencia = item.getFrequencia();
				this.tipoAprazamento = item.getTipoFreqAprazamento();
				if (item.getComponenteSanguineo() != null) {
					this.codigoComposto = DominioTipo.C
							+ item.getComponenteSanguineo().getCodigo();
					this.compSanguineoProcedHemoterapicoVO = new CompSanguineoProcedHemoterapicoVO();
					this.compSanguineoProcedHemoterapicoVO.setCodigo(item.getComponenteSanguineo().getCodigo());
					this.compSanguineoProcedHemoterapicoVO.setCodigoComposto(this.codigoComposto );
					this.compSanguineoProcedHemoterapicoVO.setDescricao(item.getComponenteSanguineo().getDescricao());
					this.compSanguineoProcedHemoterapicoVO.setTipo(DominioTipo.C);
					this.compSanguineoProcedHemoterapicoVO.setAvisoAferese(item.getComponenteSanguineo().getMensSolicAferese());
					this.compSanguineoProcedHemoterapicoVO.setAvisoPrescricao(item.getComponenteSanguineo().getAvisoPrescricao());
					this.compSanguineoProcedHemoterapicoVO.setIndAferese(item.getComponenteSanguineo().getIndAferese());
					this.compSanguineoProcedHemoterapicoVO.setIndFiltrado(item.getComponenteSanguineo().getIndFiltrado());
					this.compSanguineoProcedHemoterapicoVO.setIndIrradiado(item.getComponenteSanguineo().getIndIrradiado());
					this.compSanguineoProcedHemoterapicoVO.setIndLavado(item.getComponenteSanguineo().getIndLavado());
				} else if (item.getProcedHemoterapico() != null) {
					this.codigoComposto = DominioTipo.P
							+ item.getProcedHemoterapico().getCodigo();
					this.compSanguineoProcedHemoterapicoVO = new CompSanguineoProcedHemoterapicoVO();
					this.compSanguineoProcedHemoterapicoVO.setCodigo(item.getProcedHemoterapico().getCodigo());
					this.compSanguineoProcedHemoterapicoVO.setCodigoComposto(this.codigoComposto );
					this.compSanguineoProcedHemoterapicoVO.setDescricao(item.getProcedHemoterapico().getDescricao());
					this.compSanguineoProcedHemoterapicoVO.setTipo(DominioTipo.P);
				}

				this.tipoAprazamento = item.getTipoFreqAprazamento();
				
				break;
			}
		}
	}
	
	/**
	 * Confirma edição de outro item.
	 */
	public void editarItem() {
		this.setItemFormModified(false);
		this.editarItemHemoterapico(item2Edit);
		this.setConfirmaEdicaoItemPendente(false);
		item2Edit = null;
	}
	
	public void gravar() {
		try {
			//this.exibirModal = false;

			if (this.listaItensHemoterapicos != null && !this.listaItensHemoterapicos.isEmpty()) {
				
				this.validarDados();

				for (AbsItensSolHemoterapicas item : listaItensHemoterapicos) {
					if ( ((item.getComponenteSanguineo() != null && item.getComponenteSanguineo().getIndJustificativa())
						 ||
						 (item.getProcedHemoterapico() != null && item.getProcedHemoterapico().getIndJustificativa()))							
						&& !verificaJustificativas(item)) {
						throw new ApplicationBusinessException(ManterSolicitacaoHemoterapiaControllerExceptionCode.MPM_03353);
					}
				}
				
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				
				solicitacaoHemoterapica.setIndTransfAnteriores(this.indTransfusoes);
				solicitacaoHemoterapica.setIndPacTransplantado(this.indTransplantado);
				solicitacaoHemoterapica.setIndUrgente(this.indUrgente);
				solicitacaoHemoterapica.setObservacao(this.observacao);
				solicitacaoHemoterapica.setServidor(servidorLogado);
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPUTADA, e);
				}
			
				//GRAVAR SOLICITACAO HEMOTERÁPICA
				solicitacaoHemoterapica.setItensSolHemoterapicas(listaItensHemoterapicos);
				getPrescricaoMedicaFacade().persistirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
				solicitacaoHemoterapica = this.bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq()); 
				listaItensHemoterapicos = solicitacaoHemoterapica.getItensSolHemoterapicas();
				
				if (edicaoItemHemoterapico) {
					apresentarMsgNegocio(Severity.INFO,
							ManterSolicitacaoHemoterapiaControllerExceptionCode.SOLICITACAO_HEMOTERAPICA_ALTERADA_SUCESSO.toString());
				} else {
					apresentarMsgNegocio(Severity.INFO,
							ManterSolicitacaoHemoterapiaControllerExceptionCode.SOLICITACAO_HEMOTERAPICA_INSERIDA_SUCESSO.toString());
				}
	
				this.listaSolicitacoesHemoterapias = prescricaoMedicaFacade
						.obterListaSolicitacoesHemoterapicasPelaChavePrescricao(new MpmPrescricaoMedicaId(getPrescricaoMedicaVO().getPrescricaoMedica().getId().getAtdSeq(), getPrescricaoMedicaVO().getPrescricaoMedica().getId().getSeq()));				
				for (AbsSolicitacoesHemoterapicas solHemoterapica : listaSolicitacoesHemoterapias) {
					listaSolicitacoesHemoterapiaSelecionas.put(solHemoterapica, false);
				}
				
				final BeanComparator descricaoSorter = new BeanComparator("descricaoFormatada", new NullComparator(false));
				if (listaSolicitacoesHemoterapias != null && !listaSolicitacoesHemoterapias.isEmpty()) {
					Collections.sort(listaSolicitacoesHemoterapias, descricaoSorter);
				}
				
				limpar();
				
			} else {
				throw new ApplicationBusinessException(
						ManterSolicitacaoHemoterapiaControllerExceptionCode.LISTA_ITENS_HEMOTERAPICOS_VAZIA);
	
			}
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch(BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void validarDados() throws ApplicationBusinessException {
		if(!StringUtils.isEmpty(this.observacao) && this.observacao.length() > 60) {
			throw new ApplicationBusinessException(ManterSolicitacaoHemoterapiaControllerExceptionCode.OBSERVACAO_HEMO_INVALIDA);
		}
	}
	
	private void validaDadosAdicionar() throws ApplicationBusinessException {
		// Verificação quantidade ml, quantidade unidade e checkboxes
		if ( (compSanguineoProcedHemoterapicoVO == null || DominioTipo.P.equals(compSanguineoProcedHemoterapicoVO.getTipo())) 
				&& indAferese && indDesleucocitado
				&& indIrradiado && indLavado
				&& quantidadeUnidade != null && quantidadeMl != null) {
			throw new ApplicationBusinessException(
					ManterSolicitacaoHemoterapiaControllerExceptionCode.ITENS_SELECIONADOS_NAO_PERMITIDOS_PARA_PROCEDIMENTO);
		} else if (DominioTipo.C.equals(compSanguineoProcedHemoterapicoVO.getTipo())){
			if ((quantidadeUnidade != null && quantidadeMl != null)||(quantidadeUnidade == null && quantidadeMl == null)) {
				throw new ApplicationBusinessException(
						ManterSolicitacaoHemoterapiaControllerExceptionCode.ABS_00829);
			}
		}

		// Verificação de combos de aprazamento
		if ((tipoAprazamento != null
				|| frequencia != null || quantidadeAplicacoes != null)
				&& (tipoAprazamento == null
						|| frequencia == null || quantidadeAplicacoes == null)) {
			throw new ApplicationBusinessException(
					ManterSolicitacaoHemoterapiaControllerExceptionCode.ABS_00384);
		}
		
		if(!StringUtils.isEmpty(observacao) && observacao.length() > 60) {
			throw new ApplicationBusinessException(ManterSolicitacaoHemoterapiaControllerExceptionCode.OBSERVACAO_HEMO_INVALIDA);
		}
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void adicionarItem() {
		try {

			boolean isCadastrado = false;
			//this.exibirModal = false;
			setEdicao(false);
			validaDadosAdicionar();
			
			if (this.listaItensHemoterapicos != null && !this.listaItensHemoterapicos.isEmpty()) {
				for (AbsItensSolHemoterapicas item : this.listaItensHemoterapicos) {
					if (DominioTipo.C.equals(compSanguineoProcedHemoterapicoVO.getTipo())) {
						if (item.getComponenteSanguineo()!= null && item.getComponenteSanguineo().getCodigo().equals(
								compSanguineoProcedHemoterapicoVO.getCodigo())) {
							this.apresentarMsgNegocio(
									Severity.ERROR,
									ITEM_HEMOTERAPICO
											+ item.getDescricaoFormatada()
											+ JA_ADICIONADO_A_SOLICITACAO);
							isCadastrado = true;
							break;
						}
					} else if (DominioTipo.P.equals(compSanguineoProcedHemoterapicoVO.getTipo())) {
						if (item.getProcedHemoterapico()!= null && item.getProcedHemoterapico().getCodigo().equals(
								this.compSanguineoProcedHemoterapicoVO.getCodigo())) {
							this.apresentarMsgNegocio(
									Severity.ERROR,
									ITEM_HEMOTERAPICO
											+ item.getDescricaoFormatada()
											+ JA_ADICIONADO_A_SOLICITACAO);
							isCadastrado = true;
							break;
						}
					}
				}
			}
			if (!isCadastrado) {
	
				AbsItensSolHemoterapicas itemSolHemoterapica = new AbsItensSolHemoterapicas();
				if (DominioTipo.P.equals(compSanguineoProcedHemoterapicoVO.getTipo())) {
					itemSolHemoterapica
							.setProcedHemoterapico(bancoDeSangueFacade
									.obterProcedHemoterapicoPorCodigo(this.compSanguineoProcedHemoterapicoVO
											.getCodigo()));
					//exibirModal = itemSolHemoterapica.getProcedHemoterapico().getIndJustificativa();

				} else if (DominioTipo.C.equals(compSanguineoProcedHemoterapicoVO.getTipo()))  {
					itemSolHemoterapica
							.setComponenteSanguineo(bancoDeSangueFacade
									.obterComponeteSanguineoPorCodigo(this.compSanguineoProcedHemoterapicoVO
											.getCodigo()));
					//exibirModal = itemSolHemoterapica.getComponenteSanguineo().getIndJustificativa();
				}
				
				itemSolHemoterapica.setQtdeMl(this.quantidadeMl);
				itemSolHemoterapica.setQtdeUnidades(this.quantidadeUnidade);
				itemSolHemoterapica.setQtdeAplicacoes(this.quantidadeAplicacoes);
				itemSolHemoterapica.setIndAferese(this.indAferese);
				itemSolHemoterapica.setIndLavado(this.indLavado);
				itemSolHemoterapica.setIndIrradiado(this.indIrradiado);
				itemSolHemoterapica.setIndFiltrado(this.indDesleucocitado);
				itemSolHemoterapica.setFrequencia(this.frequencia);
				itemSolHemoterapica.setTipoFreqAprazamento(this.tipoAprazamento);
				
				//GRAVAR E GERAR JUSTIFICATICAS
				listaGruposDeJustificativas = bancoDeSangueFacade.listarGruposJustifcativasDoComponenteOuProcedimento((itemSolHemoterapica.getProcedHemoterapico()!=null)?itemSolHemoterapica.getProcedHemoterapico().getCodigo():null, (itemSolHemoterapica.getComponenteSanguineo()!=null)?itemSolHemoterapica.getComponenteSanguineo().getCodigo():null);
				List<AbsItemSolicitacaoHemoterapicaJustificativa> listaItemSolicitacaoHemoterapicaJustificativa = new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>(0);
				if (listaGruposDeJustificativas != null && !listaGruposDeJustificativas.isEmpty()) {
					for(AbsGrupoJustificativaComponenteSanguineo grupo : listaGruposDeJustificativas) {
						List<AbsJustificativaComponenteSanguineo> lista = bancoDeSangueFacade.listarJustificativasPadraoDoComponenteOuProcedimento((itemSolHemoterapica.getProcedHemoterapico()!=null)?itemSolHemoterapica.getProcedHemoterapico().getCodigo():null, (itemSolHemoterapica.getComponenteSanguineo()!=null)?itemSolHemoterapica.getComponenteSanguineo().getCodigo():null, grupo.getSeq(), prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getIndPacPediatrico());
						for (AbsJustificativaComponenteSanguineo absJustificativaComponenteSanguineo : lista) {
							AbsItemSolicitacaoHemoterapicaJustificativa itemJustificativa = new AbsItemSolicitacaoHemoterapicaJustificativa();
							itemJustificativa.setMarcado(false);
							itemJustificativa.setJustificativaComponenteSanguineo(absJustificativaComponenteSanguineo);
							itemJustificativa.setItemSolucaoHemoterapica(itemSolHemoterapica);
							listaItemSolicitacaoHemoterapicaJustificativa.add(itemJustificativa);
						}
					}
				}
	
				itemSolHemoterapica.setItemSolicitacaoHemoterapicaJustificativas(listaItemSolicitacaoHemoterapicaJustificativa);
				itemSolHemoterapica.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				itemSolHemoterapica.setSolicitacaoHemoterapica(solicitacaoHemoterapica);
				
				this.listaItensHemoterapicos.add(itemSolHemoterapica);
	
				if(solicitacaoHemoterapica.getId() == null) {
					AbsSolicitacoesHemoterapicasId id = new AbsSolicitacoesHemoterapicasId();
					id.setAtdSeq(this.prescricaoMedicaVO.getId().getAtdSeq());
					solicitacaoHemoterapica.setId(id);
					solicitacaoHemoterapica.setPrescricaoMedica(this.prescricaoMedicaVO.getPrescricaoMedica());
					solicitacaoHemoterapica.setIndPendente(DominioIndPendenteItemPrescricao.P);
					solicitacaoHemoterapica.setDthrSolicitacao(this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedicaVO.getPrescricaoMedica().getDthrInicio(),
							this.prescricaoMedicaVO.getPrescricaoMedica().getDthrFim()) ? this.prescricaoMedicaVO.getPrescricaoMedica().getDthrMovimento() : this.prescricaoMedicaVO.getPrescricaoMedica().getDthrInicio());
					solicitacaoHemoterapica.setDthrFim(null);
					solicitacaoHemoterapica.setIndColetarAmostra(false);
				}
				
				solicitacaoHemoterapica.setIndTransfAnteriores(this.indTransfusoes);
				solicitacaoHemoterapica.setIndPacTransplantado(this.indTransplantado);
				solicitacaoHemoterapica.setIndUrgente(this.indUrgente);
				solicitacaoHemoterapica.setObservacao(this.observacao);
				solicitacaoHemoterapica.setItensSolHemoterapicas(listaItensHemoterapicos);
				solicitacaoHemoterapica.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				solicitacaoHemoterapica.setIndSituacaoColeta(DominioSituacaoColeta.N);
				solicitacaoHemoterapica.setIndResponsavelColeta(DominioResponsavelColeta.N);
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPUTADA, e);
				}
				
				//GRAVAR SOLICITACAO HEMOTERÁPICA
				getPrescricaoMedicaFacade().persistirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
				solicitacaoHemoterapica = this.bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq());
				listaItensHemoterapicos = solicitacaoHemoterapica.getItensSolHemoterapicas();
				
				limpaItemForm();
				
				this.exibirModalJustificativas(itemSolHemoterapica);
			}
		} catch(BaseException e) {
			//this.exibirModal = false;
			apresentarExcecaoNegocio(e);
		} catch(BaseRuntimeException e) {
			//this.exibirModal = false;
			apresentarExcecaoNegocio(e);
		}
		
		/*if (this.exibirModal){
			this.openDialog("modalJustificativasWG");
		}*/
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void alterarItem() {
		try {
			validaDadosAdicionar();
			boolean isCadastrado = false;
			int indiceAux = 0;		
			this.setEdicao(false);
			//this.exibirModal = false;
			
			if (!this.listaItensHemoterapicos.isEmpty()) {
				for (AbsItensSolHemoterapicas item : this.listaItensHemoterapicos) {
					indiceAux++;
					if (DominioTipo.C.equals(compSanguineoProcedHemoterapicoVO.getTipo())) {
						if (item.getComponenteSanguineo() != null
								&& item.getComponenteSanguineo().getCodigo()
										.equals(
												compSanguineoProcedHemoterapicoVO
														.getCodigo())
								&& this.indice != indiceAux) {
							this.apresentarMsgNegocio(
									Severity.ERROR,
									ITEM_HEMOTERAPICO
											+ item.getDescricaoFormatada()
											+ JA_ADICIONADO_A_SOLICITACAO);
							isCadastrado = true;
							break;
						}
					} else if (DominioTipo.P.equals(compSanguineoProcedHemoterapicoVO.getTipo())) {
						if (item.getProcedHemoterapico() != null
								&& item.getProcedHemoterapico().getCodigo().equals(
										this.compSanguineoProcedHemoterapicoVO
												.getCodigo())
								&& this.indice != indiceAux) {
							this.apresentarMsgNegocio(
									Severity.ERROR,
									ITEM_HEMOTERAPICO
											+ item.getDescricaoFormatada()
											+ JA_ADICIONADO_A_SOLICITACAO);
							isCadastrado = true;
							break;
						}
					}
				}
			}
	
			if (!isCadastrado) {
	
				boolean alterarTipoSolHem = false;
				
				if (DominioTipo.P.equals(compSanguineoProcedHemoterapicoVO.getTipo())) {
					if(this.listaItensHemoterapicos.get(this.indice - 1).getProcedHemoterapico() == null || !this.listaItensHemoterapicos.get(this.indice - 1).getProcedHemoterapico().getCodigo().equals(this.compSanguineoProcedHemoterapicoVO
						.getCodigo())) {
						alterarTipoSolHem = true;
					}
					
					this.listaItensHemoterapicos
							.get(this.indice - 1)
							.setProcedHemoterapico(
									bancoDeSangueFacade
											.obterProcedHemoterapicoPorCodigo(this.compSanguineoProcedHemoterapicoVO
													.getCodigo()));
					this.listaItensHemoterapicos.get(this.indice - 1)
							.setComponenteSanguineo(null);
				} else if (DominioTipo.C.equals(compSanguineoProcedHemoterapicoVO.getTipo())) {
	
					if(this.listaItensHemoterapicos.get(this.indice - 1).getComponenteSanguineo() == null || !this.listaItensHemoterapicos.get(this.indice - 1).getComponenteSanguineo().getCodigo().equals(this.compSanguineoProcedHemoterapicoVO
							.getCodigo())) {
							alterarTipoSolHem = true;
					}
	
					this.listaItensHemoterapicos
							.get(this.indice - 1)
							.setComponenteSanguineo(
									bancoDeSangueFacade
											.obterComponeteSanguineoPorCodigo(this.compSanguineoProcedHemoterapicoVO
													.getCodigo()));
					this.listaItensHemoterapicos.get(this.indice - 1)
							.setProcedHemoterapico(null);
				}
	
				this.listaItensHemoterapicos.get(this.indice - 1).setQtdeMl(
						this.quantidadeMl);
				this.listaItensHemoterapicos.get(this.indice - 1).setQtdeUnidades(
						this.quantidadeUnidade);
				this.listaItensHemoterapicos.get(this.indice - 1).setQtdeAplicacoes(
						this.quantidadeAplicacoes);
				this.listaItensHemoterapicos.get(this.indice - 1)
						.setQtdeAplicacoes(this.quantidadeAplicacoes);
				this.listaItensHemoterapicos.get(this.indice - 1).setIndAferese(
						this.indAferese);
				this.listaItensHemoterapicos.get(this.indice - 1).setIndLavado(
						this.indLavado);
				this.listaItensHemoterapicos.get(this.indice - 1).setIndIrradiado(
						this.indIrradiado);
				this.listaItensHemoterapicos.get(this.indice - 1).setIndFiltrado(
						this.indDesleucocitado);
				this.listaItensHemoterapicos.get(this.indice - 1).setFrequencia(
						this.frequencia);
				this.listaItensHemoterapicos.get(this.indice - 1)
						.setTipoFreqAprazamento(this.tipoAprazamento);

				if(alterarTipoSolHem) {
					for (AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa : this.listaItensHemoterapicos.get(this.indice - 1).getItemSolicitacaoHemoterapicaJustificativas()) {
						if(itemSolicitacaoHemoterapicaJustificativa.getId() != null && itemSolicitacaoHemoterapicaJustificativa.getId().getIshSequencia() != null && itemSolicitacaoHemoterapicaJustificativa.getId().getIshSheSeq() != null) {
							this.prescricaoMedicaFacade.excluirJustificativaItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapicaJustificativa);
						}
					}

					//GRAVAR E GERAR JUSTIFICATICAS
					listaGruposDeJustificativas = bancoDeSangueFacade.listarGruposJustifcativasDoComponenteOuProcedimento((this.listaItensHemoterapicos.get(this.indice - 1).getProcedHemoterapico()!=null)?this.listaItensHemoterapicos.get(this.indice - 1).getProcedHemoterapico().getCodigo():null, (this.listaItensHemoterapicos.get(this.indice - 1).getComponenteSanguineo()!=null)?this.listaItensHemoterapicos.get(this.indice - 1).getComponenteSanguineo().getCodigo():null);
					List<AbsItemSolicitacaoHemoterapicaJustificativa> listaItemSolicitacaoHemoterapicaJustificativa = new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>(0);
					if (listaGruposDeJustificativas != null && !listaGruposDeJustificativas.isEmpty()) {
						for(AbsGrupoJustificativaComponenteSanguineo grupo : listaGruposDeJustificativas) {
							List<AbsJustificativaComponenteSanguineo> lista = bancoDeSangueFacade.listarJustificativasPadraoDoComponenteOuProcedimento((this.listaItensHemoterapicos.get(this.indice - 1).getProcedHemoterapico()!=null)?this.listaItensHemoterapicos.get(this.indice - 1).getProcedHemoterapico().getCodigo():null, (this.listaItensHemoterapicos.get(this.indice - 1).getComponenteSanguineo()!=null)?this.listaItensHemoterapicos.get(this.indice - 1).getComponenteSanguineo().getCodigo():null, grupo.getSeq(), this.listaItensHemoterapicos.get(this.indice - 1).getSolicitacaoHemoterapica().getPrescricaoMedica().getAtendimento().getIndPacPediatrico());
							for (AbsJustificativaComponenteSanguineo absJustificativaComponenteSanguineo : lista) {
								AbsItemSolicitacaoHemoterapicaJustificativa itemJustificativa = new AbsItemSolicitacaoHemoterapicaJustificativa();
								itemJustificativa.setMarcado(false);
								itemJustificativa.setJustificativaComponenteSanguineo(absJustificativaComponenteSanguineo);
								itemJustificativa.setItemSolucaoHemoterapica(this.listaItensHemoterapicos.get(this.indice - 1));
								listaItemSolicitacaoHemoterapicaJustificativa.add(itemJustificativa);
							}
						}
					}
					this.listaItensHemoterapicos.get(this.indice - 1).setItemSolicitacaoHemoterapicaJustificativas(listaItemSolicitacaoHemoterapicaJustificativa);
				}
				
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				
				this.listaItensHemoterapicos.get(this.indice - 1).setServidor(servidorLogado);
				this.listaItensHemoterapicos.get(this.indice - 1).setSolicitacaoHemoterapica(solicitacaoHemoterapica);
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPUTADA, e);
				}
	
				//GRAVAR SOLICITACAO HEMOTERÁPICA
				getPrescricaoMedicaFacade().persistirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
				solicitacaoHemoterapica = this.bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq());
				listaItensHemoterapicos = solicitacaoHemoterapica.getItensSolHemoterapicas();
				
				limpaItemForm();
			}
		} catch (BaseException e) {
			//this.exibirModal = false;
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean validarItemSelecionado(AbsItensSolHemoterapicas item) {
		if(item.getComponenteSanguineo() != null && item.getComponenteSanguineo().getCodigo().equals(codigoItem)) {
			return true;
		} else if(item.getProcedHemoterapico() != null && item.getProcedHemoterapico().getCodigo().equals(codigoItem)) {
			return true;
		}
		return false;
	}
	
	public void limpaItemForm(){
		this.itemId = null;
		this.codigoItem = null;
		this.quantidadeMl=null;
		this.quantidadeUnidade=null;
		this.quantidadeAplicacoes = null;
		this.indAferese=null;
		this.indLavado=null;
		this.indIrradiado=null;
		this.indDesleucocitado=null;
		this.frequencia=null;
		this.tipoAprazamento=null;
		this.codigoComposto=null;
		this.compSanguineoProcedHemoterapicoVO=null;
		this.setItemFormModified(false);
		this.setConfirmaEdicaoItemPendente(false);
	}
	
	/*public void desabilitarExibicaoModal() {
		this.exibirModal = false;
	}*/
	
	public List<CompSanguineoProcedHemoterapicoVO> getListaItens()
			throws ApplicationBusinessException {
		return prescricaoMedicaFacade
				.pesquisarCompSanguineoProcedHemoterapico();
	}

	public void removerItemHemoterapico() {
		try {
			this.itemId = null;
			this.setEdicao(false);
	
			AbsItensSolHemoterapicas item = null;

			if (this.atdSeq != null && this.seqShe != null && this.seqItem != null) {
				AbsItensSolHemoterapicasId itensSolHemoterapicasId = new AbsItensSolHemoterapicasId(this.atdSeq, this.seqShe, this.seqItem);
				AbsItensSolHemoterapicas itensSolHemoterapicas = new AbsItensSolHemoterapicas();
				itensSolHemoterapicas.setId(itensSolHemoterapicasId);
				
				if (this.listaItensHemoterapicos.indexOf(itensSolHemoterapicas) != -1) {
					item = this.listaItensHemoterapicos.get(this.listaItensHemoterapicos.indexOf(itensSolHemoterapicas));						
				}
			} else {
				item = this.listaItensHemoterapicos.get(this.atdSeq);
			}
			
			if (this.listaItensHemoterapicos.size() > 1) {
				for (Iterator<AbsItensSolHemoterapicas> i = this.listaItensHemoterapicos
						.iterator(); i.hasNext();) {
					AbsItensSolHemoterapicas itemHem = (AbsItensSolHemoterapicas) i
							.next();

					if ((itemHem.getComponenteSanguineo() != null
							&& item.getComponenteSanguineo() != null && itemHem
							.getComponenteSanguineo().getCodigo().equals(
									item.getComponenteSanguineo().getCodigo()))
							|| (itemHem.getProcedHemoterapico() != null
									&& item.getProcedHemoterapico() != null && itemHem
									.getProcedHemoterapico().getCodigo()
									.equals(
											item.getProcedHemoterapico()
													.getCodigo()))) {
		
						i.remove();
						solicitacaoHemoterapica.setItensSolHemoterapicas(this.listaItensHemoterapicos);
						break;
					}
				}
					
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPUTADA, e);
				}
				getPrescricaoMedicaFacade().persistirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
				solicitacaoHemoterapica = this.bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq());
				listaItensHemoterapicos = solicitacaoHemoterapica.getItensSolHemoterapicas();
				
			} else {
				throw new ApplicationBusinessException(ManterSolicitacaoHemoterapiaControllerExceptionCode.IMPOSSIVEL_EXCLUIR_APENAS_UM_ITEM_CADASTRADO);
			}
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Throwable e) { //NOPMD
			e.printStackTrace(); //NOPMD
		}
	}

	public void editarItemSolicitacao(MpmItemPrescricaoMdto item) {
		this.setEdicao(true);
		this.indice = 0;
	}

	public void cancelarEdiItem() {
		this.setEdicao(false);
		limpaItemForm();
		
	}
	
	/* Usado na modal de justificativas
	public void associarJustificativas(){
		
	} */
	
	public void exibirModalJustificativas(AbsItensSolHemoterapicas itemSolHemo) {
		if (itemSolHemo.getComponenteSanguineo() != null) {
			this.componenteSanguineoCodigo = itemSolHemo.getComponenteSanguineo().getCodigo();
			this.procedHemoterapicoCodigo = null;
		} else if (itemSolHemo.getProcedHemoterapico() != null) {
			this.componenteSanguineoCodigo = null;
			this.procedHemoterapicoCodigo = itemSolHemo.getProcedHemoterapico().getCodigo();
		}
		this.itensSolHemoterapicas = itemSolHemo;

		try {
			AipPacientes paciente = this.pacienteFacade.obterPacientePorProntuario(prescricaoMedicaVO.getProntuario());

			this.descricaoJustificativaSolicitacao = bancoDeSangueFacade.buscarDescricaoJustificativaSolicitacao(
					procedHemoterapicoCodigo, componenteSanguineoCodigo);
			this.exames = getPrescricaoMedicaFacade().buscaResultadoExames(
					paciente
					, this.componenteSanguineoCodigo
					, this.procedHemoterapicoCodigo);

			justificativasSelecionadas = new HashMap<Short, List<JustificativaComponenteSanguineoVO>>();

			listaGruposDeJustificativas = bancoDeSangueFacade.listarGruposJustifcativasDoComponenteOuProcedimento(
					procedHemoterapicoCodigo
					, componenteSanguineoCodigo);

			if (listaGruposDeJustificativas != null && !listaGruposDeJustificativas.isEmpty()) {
				for (AbsGrupoJustificativaComponenteSanguineo grupo : listaGruposDeJustificativas) {
					List<JustificativaComponenteSanguineoVO> listaVO = new ArrayList<JustificativaComponenteSanguineoVO>(0);
					List<AbsJustificativaComponenteSanguineo> lista = 
							bancoDeSangueFacade.listarJustificativasPadraoDoComponenteOuProcedimento(
									procedHemoterapicoCodigo
									, componenteSanguineoCodigo
									, grupo.getSeq()
									, itemSolHemo.getSolicitacaoHemoterapica().getPrescricaoMedica().getAtendimento().getIndPacPediatrico());
					for (AbsJustificativaComponenteSanguineo absJustificativaComponenteSanguineo : lista) {
						JustificativaComponenteSanguineoVO vo = new JustificativaComponenteSanguineoVO();

						AbsItemSolicitacaoHemoterapicaJustificativaId itemSolicitacaoHemoterapicaJustificativaId = 
								new AbsItemSolicitacaoHemoterapicaJustificativaId(
										itemSolHemo.getId().getSheAtdSeq()
										, itemSolHemo.getId().getSheSeq()
										, itemSolHemo.getId().getSequencia()
										, absJustificativaComponenteSanguineo.getSeq());
						AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa = 
								new AbsItemSolicitacaoHemoterapicaJustificativa();
						itemSolicitacaoHemoterapicaJustificativa.setId(itemSolicitacaoHemoterapicaJustificativaId);
						
						
						int indexOfItemSolHemoJustificativa = itemSolHemo.getItemSolicitacaoHemoterapicaJustificativas()
								.indexOf(itemSolicitacaoHemoterapicaJustificativa);
						if (indexOfItemSolHemoJustificativa != -1) {
							vo.setJustificativaComponenteSanguineo(absJustificativaComponenteSanguineo);
							vo.setItemSolicitacaoHemoterapicaJustificativa(
								itemSolHemo.getItemSolicitacaoHemoterapicaJustificativas().get(indexOfItemSolHemoJustificativa)
							);
						}
						
						if (vo.getItemSolicitacaoHemoterapicaJustificativa() != null) {
							vo.setMarcado(vo.getItemSolicitacaoHemoterapicaJustificativa().getMarcado());
							vo.setDescricaoLivre(vo.getItemSolicitacaoHemoterapicaJustificativa().getDescricaoLivre());
						}
						
						listaVO.add(vo);
					}// for listarJustificativasPadraoDoComponenteOuProcedimento
					justificativasSelecionadas.put(grupo.getSeq(), listaVO);
				}// for listaGruposDeJustificativas
			}//if listaGruposDeJustificativas
			//this.exibirModal = true;
			super.openDialog("modalJustificativasWG");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarJustificativas() {
		try {
			if (solicitacaoHemoterapica.getId() == null || solicitacaoHemoterapica.getId().getSeq() == null) {
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPUTADA, e);
				}
				
				//GRAVAR SOLICITACAO HEMOTERÁPICA
				getPrescricaoMedicaFacade().persistirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
				solicitacaoHemoterapica = this.bancoDeSangueFacade.obterSolicitacoesHemoterapicasComItensSolicitacoes(solicitacaoHemoterapica.getPrescricaoMedica().getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq());
				listaItensHemoterapicos = solicitacaoHemoterapica.getItensSolHemoterapicas();
			}
			
			List<AbsItemSolicitacaoHemoterapicaJustificativa> listaItemSolicitacaoHemoterapicaJustificativa =
					prescricaoMedicaFacade.gravarItemSolicitacaoHemoterapicaJustificativa(justificativasSelecionadas);
			
			listaItensHemoterapicos.get(
					listaItensHemoterapicos.indexOf(itensSolHemoterapicas))
					.setItemSolicitacaoHemoterapicaJustificativas(
							listaItemSolicitacaoHemoterapicaJustificativa);
			
			super.closeDialog("modalJustificativasWG");
			
			//this.exibirModal = false;
			apresentarMsgNegocio(Severity.INFO,"SUCESSO_GRAVAR_SOLICITACAO_HEMOTERAPICA_JUSTIFICATIVA");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEdicaoJustificativas() {
		if (this.justificativasSelecionadas != null) {
			this.justificativasSelecionadas.clear();
		}
		//this.exibirModal = false;
	}
	
	/*public void ocultarModalJustificativas() {
		this.exibirModal = false;
	}*/
	
	public Boolean verificaJustificativas(AbsItensSolHemoterapicas itemSolHemo) {
		boolean encontrou = false;

		if (itemSolHemo.getComponenteSanguineo() != null) {
			for (AbsItemSolicitacaoHemoterapicaJustificativa itemJustificativa : itemSolHemo
					.getItemSolicitacaoHemoterapicaJustificativas()) {
				if (itemJustificativa.getMarcado()) {
					encontrou = true;
					break;
				}
			}
		}
		if (itemSolHemo.getProcedHemoterapico() != null) {
//			List<AbsItemSolicitacaoHemoterapicaJustificativa> listaJustificativas = null;
//			this.justificativasSelecionadas.va
			
			for (AbsItemSolicitacaoHemoterapicaJustificativa itemJustificativa : itemSolHemo
					.getItemSolicitacaoHemoterapicaJustificativas()) {
				if (itemJustificativa.getMarcado()) {
					encontrou = true;
					break;
				}
			}
		}
		
		return encontrou;
	}

	public String cancelar() {
		this.confirmaVoltar = false;
		
		if (this.isFormModified()) {
			this.confirmaVoltar = true;
			openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}

		return voltar();
	}

	public String voltar() {
		confirmaVoltar = false;
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}
	
	public boolean isConfirmaVoltar() {
		return confirmaVoltar;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}
	
	public String buscaDescricaoTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento
				.getDescricaoSintaxeFormatada(this.frequencia)
				: "";
	}

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null
				&& this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
		
		this.setItemFormModified(true);
	}
	
	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(
			String strPesquisa) {
		return this.prescricaoMedicaFacade
				.buscarTipoFrequenciaAprazamentoHemoterapico((String) strPesquisa);
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	/*public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public String getMensagemExibicaoModal() {
		return mensagemExibicaoModal;
	}

	public void setMensagemExibicaoModal(String mensagemExibicaoModal) {
		this.mensagemExibicaoModal = mensagemExibicaoModal;
	}*/

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public AbsSolicitacoesHemoterapicas getSolicitacaoHemoterapica() {
		return solicitacaoHemoterapica;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica) {
		this.solicitacaoHemoterapica = solicitacaoHemoterapica;
	}

	public void setEdicaoItemHemoterapico(Boolean edicaoItemHemoterapico) {
		this.edicaoItemHemoterapico = edicaoItemHemoterapico;
	}

	public Boolean getEdicaoItemHemoterapico() {
		return edicaoItemHemoterapico;
	}

	public Byte getQuantidadeUnidade() {
		return quantidadeUnidade;
	}

	public void setQuantidadeUnidade(Byte quantidadeUnidade) {
		this.quantidadeUnidade = quantidadeUnidade;
	}

	public Short getQuantidadeMl() {
		return quantidadeMl;
	}

	public void setQuantidadeMl(Short quantidadeMl) {
		this.quantidadeMl = quantidadeMl;
	}

	public Short getQuantidadeAplicacoes() {
		return quantidadeAplicacoes;
	}

	public void setQuantidadeAplicacoes(Short quantidadeAplicacoes) {
		this.quantidadeAplicacoes = quantidadeAplicacoes;
	}

	public void setCompSanguineoProcedHemoterapicoVO(
			CompSanguineoProcedHemoterapicoVO compSanguineoProcedHemoterapicoVO) {
		this.compSanguineoProcedHemoterapicoVO = compSanguineoProcedHemoterapicoVO;
	}

	public CompSanguineoProcedHemoterapicoVO getCompSanguineoProcedHemoterapicoVO() {
		return compSanguineoProcedHemoterapicoVO;
	}

	public Boolean getIndIrradiado() {
		return indIrradiado;
	}

	public void setIndIrradiado(Boolean indIrradiado) {
		this.indIrradiado = indIrradiado;
	}

	public Boolean getIndDesleucocitado() {
		return indDesleucocitado;
	}

	public void setIndDesleucocitado(Boolean indDesleucocitado) {
		this.indDesleucocitado = indDesleucocitado;
	}

	public Boolean getIndLavado() {
		return indLavado;
	}

	public void setIndLavado(Boolean indLavado) {
		this.indLavado = indLavado;
	}

	public Boolean getIndAferese() {
		return indAferese;
	}

	public void setIndAferese(Boolean indAferese) {
		this.indAferese = indAferese;
	}

	public Boolean getIndTransfusoes() {
		return indTransfusoes;
	}

	public void setIndTransfusoes(Boolean indTransfusoes) {
		this.indTransfusoes = indTransfusoes;
	}

	public Boolean getIndTransplantado() {
		return indTransplantado;
	}

	public void setIndTransplantado(Boolean indTransplantado) {
		this.indTransplantado = indTransplantado;
	}

	public Boolean getIndUrgente() {
		return indUrgente;
	}

	public void setIndUrgente(Boolean indUrgente) {
		this.indUrgente = indUrgente;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public void setListaSolicitacoesHemoterapias(
			List<AbsSolicitacoesHemoterapicas> listaSolicitacoesHemoterapias) {
		this.listaSolicitacoesHemoterapias = listaSolicitacoesHemoterapias;
	}

	public List<AbsSolicitacoesHemoterapicas> getListaSolicitacoesHemoterapias() {
		return listaSolicitacoesHemoterapias;
	}

	public void setListaSolicitacoesHemoterapiaSelecionas(
			Map<AbsSolicitacoesHemoterapicas, Boolean> listaSolicitacoesHemoterapiaSelecionas) {
		this.listaSolicitacoesHemoterapiaSelecionas = listaSolicitacoesHemoterapiaSelecionas;
	}

	public Map<AbsSolicitacoesHemoterapicas, Boolean> getListaSolicitacoesHemoterapiaSelecionas() {
		return listaSolicitacoesHemoterapiaSelecionas;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public void setListaItensHemoterapicos(
			List<AbsItensSolHemoterapicas> listaItensHemoterapicos) {
		this.listaItensHemoterapicos = listaItensHemoterapicos;
	}

	public List<AbsItensSolHemoterapicas> getListaItensHemoterapicos() {
		return listaItensHemoterapicos;
	}

	public void setItemId(AbsItensSolHemoterapicasId itemId) {
		this.itemId = itemId;
	}

	public AbsItensSolHemoterapicasId getItemId() {
		return itemId;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeqShe() {
		return seqShe;
	}

	public void setSeqShe(Integer seqShe) {
		this.seqShe = seqShe;
	}

	public Short getSeqItem() {
		return seqItem;
	}

	public void setSeqItem(Short seqItem) {
		this.seqItem = seqItem;
	}

	public void setSolicitacaoHemoterapicaId(
			AbsSolicitacoesHemoterapicasId solicitacaoHemoterapicaId) {
		this.solicitacaoHemoterapicaId = solicitacaoHemoterapicaId;
	}

	public AbsSolicitacoesHemoterapicasId getSolicitacaoHemoterapicaId() {
		return solicitacaoHemoterapicaId;
	}

	public void setCodigoComposto(String codigoComposto) {
		this.codigoComposto = codigoComposto;
	}

	public String getCodigoComposto() {
		return codigoComposto;
	}

	public List<AbsGrupoJustificativaComponenteSanguineo> getListaGruposDeJustificativas() {
		return listaGruposDeJustificativas;
	}

	public void setListaGruposDeJustificativas(
			List<AbsGrupoJustificativaComponenteSanguineo> listaGruposDeJustificativas) {
		this.listaGruposDeJustificativas = listaGruposDeJustificativas;
	}

	public String getProcedHemoterapicoCodigo() {
		return procedHemoterapicoCodigo;
	}

	public void setProcedHemoterapicoCodigo(String procedHemoterapicoCodigo) {
		this.procedHemoterapicoCodigo = procedHemoterapicoCodigo;
	}

	public String getComponenteSanguineoCodigo() {
		return componenteSanguineoCodigo;
	}

	public void setComponenteSanguineoCodigo(String componenteSanguineoCodigo) {
		this.componenteSanguineoCodigo = componenteSanguineoCodigo;
	}

	public Map<Short, List<JustificativaComponenteSanguineoVO>> getJustificativasSelecionadas() {
		return justificativasSelecionadas;
	}

	public void setJustificativasSelecionadas(
			Map<Short, List<JustificativaComponenteSanguineoVO>> justificativasSelecionadas) {
		this.justificativasSelecionadas = justificativasSelecionadas;
	}

	public List<String> getExames() {
		return exames;
	}

	public void setExames(List<String> exames) {
		this.exames = exames;
	}

	public String getDescricaoJustificativaSolicitacao() {
		return descricaoJustificativaSolicitacao;
	}

	public void setDescricaoJustificativaSolicitacao(
			String descricaoJustificativaSolicitacao) {
		this.descricaoJustificativaSolicitacao = descricaoJustificativaSolicitacao;
	}
	
	public String getCodigoItem() {
		return codigoItem;
	}

	public void setCodigoItem(String codigoItem) {
		this.codigoItem = codigoItem;
	}

	public AbsSolicitacoesHemoterapicasId getIdSolicitacao() {
		return idSolicitacao;
	}

	public void setIdSolicitacao(AbsSolicitacoesHemoterapicasId idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public boolean isItemFormModified() {
		return itemFormModified;
	}

	public void setItemFormModified(boolean ItemFormModified) {
		this.itemFormModified = ItemFormModified;
	}

	public boolean isConfirmaEdicaoItemPendente() {
		return confirmaEdicaoItemPendente;
	}

	public void setConfirmaEdicaoItemPendente(boolean confirmaEdicaoItemPendente) {
		this.confirmaEdicaoItemPendente = confirmaEdicaoItemPendente;
	}

	public boolean isConfirmaEdicaoPendente() {
		return confirmaEdicaoPendente;
	}

	public void setConfirmaEdicaoPendente(boolean confirmaEdicaoPendente) {
		this.confirmaEdicaoPendente = confirmaEdicaoPendente;
	}

	public boolean isFormModified() {
		return formModified || this.isItemFormModified();
	}

	public void setFormModified(boolean formModified) {
		this.formModified = formModified;
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(int selectedTab) {
		this.selectedTab = selectedTab;
	}

}
