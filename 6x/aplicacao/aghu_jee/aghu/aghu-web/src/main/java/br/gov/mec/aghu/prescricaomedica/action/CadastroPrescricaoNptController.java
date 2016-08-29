package br.gov.mec.aghu.prescricaomedica.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmJustificativaNpt;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricaoId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * #990
 * 
 */
public class CadastroPrescricaoNptController extends ActionController {

	private static final long serialVersionUID = -2892721742898438593L;
	private static final Log LOG = LogFactory.getLog(CadastroPrescricaoNptController.class);
	private static final String VALOR_PESO_ALTURA_INVALIDOS = "VALOR_PESO_ALTURA_INVALIDOS";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private AipPacientes paciente;
	private PrescricaoMedicaVO prescricaoMedicaVO;
	private MpmJustificativaNpt justificativa;
	private boolean forceDialog;
	private MpmPrescricaoNptVO prescricaoNptVO = new MpmPrescricaoNptVO();
	private ItemPrescricaoMedicaVO prescricaoNptItem;
	private static final String PAGINA_SELECAO_FORMULA = "escolherFormulaNpt";
	
	private MpmComposicaoPrescricaoNptVO composicaoSelecionada;	
	
	private Boolean isFormulaPadrao = Boolean.FALSE;
	private String mensagem = "";
	private TreeNode root;
	private int idConversacaoAnterior;
	private boolean modoEdicao;
	// Modal peso altura
	private BigDecimal peso;
	private BigDecimal altura;
	private BigDecimal sc;
	private String mensagemModal;
	private MpmParamCalculoPrescricao parametroCalculo;
	private DominioTipoMedicaoPeso tipoMedicao;
	private DadosPesoAlturaVO dadosPesoAlturaVO = new DadosPesoAlturaVO();
	//#988
	private Short fnpSeq;
	private String descricaoFormula;
	private List<ItemPrescricaoMedicaVO> listaChecked;
	
	private Integer seq;
	private static final String ESPACO=" ";
	private static final String IGUAL = " = ";
	private static final String TRACO = " - ";
	
	public void iniciar() {
		if (prescricaoMedicaVO != null) {
			//ON04
			verificarPesoAltAtualizados();
			listarPrescricaoMedicaItens();
			setRoot(new DefaultTreeNode());
			criarMensagemNpt();
			//ON05
			verificarFormulaPadrao();
			if (seq != null) {
				if (prescricaoNptVO == null || prescricaoNptVO.getSeq() == null) {
					prescricaoNptVO = prescricaoMedicaFacade.buscarDadosPrescricaoNpt(prescricaoMedicaVO.getId().getAtdSeq(), seq);
					modoEdicao = true;
					justificativa = prescricaoMedicaFacade.obterJustificativaPorChavePrimaria(prescricaoNptVO.getJnpSeq());
					fnpSeq = prescricaoNptVO.getFnpSeq();
					descricaoFormula = prescricaoNptVO.getDescricaoFormula();
				}
			}
			definirComposicaoPadrao();
			popularNodo();
		}
	}
	
	public void editar() {
		prescricaoNptVO = prescricaoMedicaFacade.buscarDadosPrescricaoNpt(prescricaoNptItem.getAtendimentoSeq(), prescricaoNptItem.getItemSeq().intValue());
		modoEdicao = true;
		justificativa = prescricaoMedicaFacade.obterJustificativaPorChavePrimaria(prescricaoNptVO.getJnpSeq());
		fnpSeq = prescricaoNptVO.getFnpSeq();
		descricaoFormula = prescricaoNptVO.getDescricaoFormula();
		definirComposicaoPadrao();
		popularNodo();
	}

	private void definirComposicaoPadrao() {
		if(this.getComposicoes() != null && !this.getComposicoes().isEmpty()){
			this.composicaoSelecionada = this.getComposicoes().get(0);
		}
	}
	
	/**
	 * Excluir
	 */
	public void excluirSelecionados() {
		String nomeMicrocomputador;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				for (ItemPrescricaoMedicaVO item : prescricaoMedicaVO.getItens()) {
					if (item.getExcluir()) {
						prescricaoMedicaFacade.excluirPrescricaoNptPorItem(item, nomeMicrocomputador);
					}
				}
				listarPrescricaoMedicaItens();
				limpar();
				iniciar();
				apresentarMsgNegocio("MSG_EXCLUIDO_SUCESSO");
			} catch (UnknownHostException e) {
				LOG.error("Exceção capturada:", e);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
	}
	
	public boolean habilitarBotaoExcluir(){
		boolean disabled = true;
		if(prescricaoMedicaVO != null){
			for (ItemPrescricaoMedicaVO item : prescricaoMedicaVO.getItens()) {
				if (item.getExcluir() != null) {
					if(item.getExcluir()){
						disabled = false;
					}
				}
			}
		}
		return disabled;
	}

	public String voltar() {
		this.limpar();
		return "manterPrescricaoMedica";
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	//TODO parse dados VO	
	public void gravar() {
		if(justificativa == null) {
//			Concatena mensagem de campo obrigatório.
			verificaPreenchimentoFormula();
			apresentarMsgNegocio(Severity.ERROR,"CAMPO_OBRIGATORIO", "Justificativa");
			return;
		} else if (!verificaPreenchimentoFormula()) {
			return;
		}
		
		if(prescricaoMedicaFacade.temComposicoesComponentesNulos(prescricaoNptVO)){
			org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('modalItensNulosWG').show();");
		} else {
			persistirPrescricaoNpt(false);
		}
	}

	private boolean verificaPreenchimentoFormula() {
		if (fnpSeq == null || fnpSeq.shortValue() == 0) {
			//Concatena mensagem de campo obrigatório.
			apresentarMsgNegocio(Severity.ERROR,"CAMPO_OBRIGATORIO", getBundle().getString("LABEL_DESC_FORMULA_NPT"));
			return false;
		}
		return true;
	}

	private void persistirPrescricaoNpt(Boolean desconsiderarItensNulos)  {
		String nomeMicrocomputador;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			prescricaoMedicaFacade.persistirPrescricaoNpt(fnpSeq, descricaoFormula, justificativa, prescricaoMedicaVO, prescricaoNptVO, nomeMicrocomputador, desconsiderarItensNulos);
			this.listarPrescricaoMedicaItens();
			this.limpar();
			iniciar();
			apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_GRAVACAO_NPT");
		} catch (UnknownHostException e) {
			LOG.error("Exceção capturada:", e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void deletarItensNulos() {
		this.persistirPrescricaoNpt(true);
	}
	

	public void limpar(){
		paciente= null;
		justificativa = null;
		prescricaoNptVO = new MpmPrescricaoNptVO();
		prescricaoNptItem  = null;		
		composicaoSelecionada = null;
		isFormulaPadrao = Boolean.FALSE;
		mensagem = "";
		modoEdicao = false;
		peso = null;
		altura = null;
		sc = null;
		mensagemModal = "";
		parametroCalculo = null;
		tipoMedicao = null;
		fnpSeq = null;
		descricaoFormula = "";
		seq = null;
		this.forceDialog = false;
		this.popularNodo();
	}
	
	private void verificarFormulaPadrao() {
		this.isFormulaPadrao = prescricaoMedicaFacade.isFormulaPadraoOuLivre(fnpSeq);
	}
	
	private void criarMensagemNpt() {
		this.mensagem = prescricaoMedicaFacade.montarMensagemPrescricaoNpt(this.prescricaoMedicaVO.getId().getAtdSeq());
		
	}
	
	public void persistirDadosPesoAltura() {
		try {

			peso = BigDecimal.ZERO.equals(peso) ? null : peso;
			altura = BigDecimal.ZERO.equals(altura) ? null : altura;
            if (altura != null && !validaIntervalo(altura, 0, 1000)) {
                apresentarMsgNegocio(Severity.ERROR, VALOR_PESO_ALTURA_INVALIDOS);
                return;
            }
            if (peso != null && !validaIntervalo(peso, 0, 1000)) {
                apresentarMsgNegocio(Severity.ERROR, VALOR_PESO_ALTURA_INVALIDOS);
                return;
            }
			this.prescricaoMedicaFacade.atualizarDadosPesoAltura(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getPaciente().getCodigo(), prescricaoMedicaVO.getId().getAtdSeq(), peso, tipoMedicao, altura, null, sc, sc, dadosPesoAlturaVO);
			setParametroCalculo(this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq()));
			closeDialog("modalPesoAlturaWG");
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	private void verificarPesoAltAtualizados() {
		Boolean isAtualizado = prescricaoMedicaFacade.possuiDadosPesoAlturaDia(prescricaoMedicaVO.getId().getAtdSeq());
		if(isAtualizado) {
			buscarPesoAltura();
		} else {
			mostrarModalPesoAltura();
		}
	}

	public void mostrarModalPesoAltura() {
		processarDadosPesoAltura();
		org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('modalPesoAlturaWG').show();");
	}
	
	private void buscarPesoAltura() {
		setParametroCalculo(this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq()));
		if(getParametroCalculo() != null) {
			if(getParametroCalculo().getAipPesoPaciente() != null) {
				peso = getParametroCalculo().getAipPesoPaciente().getPeso();
				tipoMedicao = getParametroCalculo().getAipPesoPaciente().getRealEstimado();
			}
			if(getParametroCalculo().getAipAlturaPaciente() != null) {
				altura = getParametroCalculo().getAipAlturaPaciente().getAltura();
		}
			sc = getParametroCalculo().getSc();
		}
	}	
	
	public void  processarDadosPesoAltura() {
		this.prescricaoMedicaFacade.inicializarCadastroPesoAltura(this.prescricaoMedicaVO.getId().getAtdSeq(), dadosPesoAlturaVO);
		
		setParametroCalculo(this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq()));
		if(getParametroCalculo() != null) {
			if(getParametroCalculo().getAipPesoPaciente() != null) {
				this.setPeso(getParametroCalculo().getAipPesoPaciente().getPeso());
				setTipoMedicao(getParametroCalculo().getAipPesoPaciente().getRealEstimado());
			}
			if(getParametroCalculo().getAipAlturaPaciente() != null) {
				this.setAltura(getParametroCalculo().getAipAlturaPaciente().getAltura());
			}
			this.setSc(getParametroCalculo().getSc());
		} else {
			setParametroCalculo(new MpmParamCalculoPrescricao());
			getParametroCalculo().setId(new MpmParamCalculoPrescricaoId(null, new Date()));
		}
		setMensagemModal(WebUtil.initLocalizedMessage("MSG_MODAL_PESO_ALTURA_MEDICAMENTO_OU_SOLUCAO",null));
	}
	
	
	public Boolean habilitarVelocidadeAdm() {
		return prescricaoMedicaFacade.isFormulaPadraoOuLivre(prescricaoNptVO.getFnpSeq());
	}
	
	
	public void selecionarComposicao(MpmComposicaoPrescricaoNptVO item) {
		this.composicaoSelecionada = item;
	}


	//TODO ON02
	public void popularFormula() {
		this.forceDialog = true;
		if(fnpSeq == null && StringUtils.isBlank(descricaoFormula)) {
			AfaFormulaNptPadrao formula = prescricaoMedicaFacade.obterFormulaPediatricao(this.prescricaoMedicaVO.getId().getAtdSeq());
			if(formula != null) {
				fnpSeq = formula.getSeq();
				descricaoFormula = formula.getDescricao();
				if(!prescricaoNptVO.getComposicoes().isEmpty()) {
					prescricaoNptVO.setComposicoes(new ArrayList<MpmComposicaoPrescricaoNptVO>());
				}
			}
		}
	}
	
	public void desativarConfirmacaoMudanca(){
		this.forceDialog = false;
	}
	
	public String selecionarFormula(){
		return PAGINA_SELECAO_FORMULA;
	}
	
	public void  listarPrescricaoMedicaItens(){
		try {
			List<ItemPrescricaoMedicaVO> itemPrescricao = prescricaoMedicaFacade.buscarItensPrescricaoMedica(prescricaoMedicaVO.getPrescricaoMedica().getId(), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
			prescricaoMedicaVO.setItens(itemPrescricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void popularNodo(){
		root = new DefaultTreeNode("Root", null);
		TreeNode informacoesPaciente =new DefaultTreeNode(montarProntuarioNomePaciente(), root);
		if (prescricaoNptVO != null) {
        TreeNode nodePrescricaoNpt = new DefaultTreeNode(getBundle().getString("LABEL_PRESCRICAO_NPT"), informacoesPaciente);
        nodePrescricaoNpt.setExpanded(true);
	        for (MpmComposicaoPrescricaoNptVO composicao: prescricaoNptVO.getComposicoes()) {
	        	TreeNode nodo = new DefaultTreeNode(composicao.getComposicaoDescricao() + ESPACO + valorFormatado(composicao.getVelocidadeAdministracao(), "###.##") +ESPACO +valorUnidadeFormata(composicao.getUnidade()), nodePrescricaoNpt);
	            if (composicao.getComponentes() != null) {
		        	for (MpmItemPrescricaoNptVO componente : composicao.getComponentes()) {
		        		nodo.getChildren().add(new DefaultTreeNode(componente.getDescricaoComponente() + IGUAL + valorFormatado(componente.getQtdePrescrita(), "##########.####") + ESPACO + valorUnidadeFormata(componente.getUnidadeComponente())));
		        		nodo.setExpanded(true);
		        	}
	            }
			}      
		}
		
		root.setExpanded(true);
		informacoesPaciente.setExpanded(true);
	}
	
	
	private String valorFormatado(BigDecimal valor, String formato){
		if (valor == null) {
			return " ";
		}
		
		DecimalFormat df = new DecimalFormat(formato);
		return df.format(valor);
	}
	private String valorUnidadeFormata(String unidade){
		if (StringUtils.isNotBlank(unidade)) {
			return unidade.toLowerCase();	
		}
		return "";
	}
	
	private String montarProntuarioNomePaciente(){
		return  CoreUtil.formataProntuario(prescricaoMedicaVO.getProntuario()) + TRACO + prescricaoMedicaVO.getNome().toUpperCase();
		
	}
	
	public String editarItens(){
		return null;
	}
	
	/**
	 * SB1
	 * @param param
	 * @return
	 */
	public List<MpmJustificativaNpt> pesquisarJustificativaNptPorDescricao(String param) {
		return this.returnSGWithCount(prescricaoMedicaFacade.pesquisarJustificativaNptPorDescricao(param), 
				prescricaoMedicaFacade.pesquisarJustificativaNptPorDescricaoCount(param));	
	}
	
	
	public void calcularSC() {
		DadosPesoAlturaVO vo = dadosPesoAlturaVO;
		if(CoreUtil.modificados(peso, vo.getPeso()) || CoreUtil.modificados(altura, vo.getAltura())) {
			sc = this.prescricaoMedicaFacade.calculaSC(prescricaoMedicaVO.getIndPacPediatrico(), peso, altura);
		}
	}

    public boolean validaIntervalo(BigDecimal valor, Integer inicioIntervaloAberto, Integer fimIntervaloAberto) {
        if (valor.compareTo(BigDecimal.valueOf(inicioIntervaloAberto.doubleValue())) <= 0
                || valor.compareTo(BigDecimal.valueOf(fimIntervaloAberto.doubleValue())) >= 0) {
            return false;
        }
        return true;
    }

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public MpmJustificativaNpt getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(MpmJustificativaNpt justificativa) {
		this.justificativa = justificativa;
	}
	
	public boolean isForceDialog() {
		return forceDialog;
	}
	
	public void setForceDialog(boolean forceDialog) {
		this.forceDialog = forceDialog;
	}

	public MpmPrescricaoNptVO getPrescricaoNptVO() {
		return prescricaoNptVO;
	}

	public void setPrescricaoNptVO(MpmPrescricaoNptVO prescricaoNptVO) {
		this.prescricaoNptVO = prescricaoNptVO;
	}

	public List<MpmComposicaoPrescricaoNptVO> getComposicoes() {
		return this.prescricaoNptVO.getComposicoes();		
	}

	public void setComposicoes(List<MpmComposicaoPrescricaoNptVO> composicoes) {
		this.prescricaoNptVO.setComposicoes(composicoes);
	}

	public MpmComposicaoPrescricaoNptVO getComposicaoSelecionada() {
		return composicaoSelecionada;
	}

	public void setComposicaoSelecionada(MpmComposicaoPrescricaoNptVO composicaoSelecionada) {
		this.composicaoSelecionada = composicaoSelecionada;
	}

	public Boolean getIsFormulaPadrao() {
		return isFormulaPadrao;
	}

	public void setIsFormulaPadrao(Boolean isFormulaPadrao) {
		this.isFormulaPadrao = isFormulaPadrao;
	}

	public List<MpmItemPrescricaoNptVO> getComponentes() {
		if (this.composicaoSelecionada == null) {
			return new ArrayList<MpmItemPrescricaoNptVO>();
		}
		return this.composicaoSelecionada.getComponentes();
	}

	public void setComponentes(List<MpmItemPrescricaoNptVO> componentes) {
		this.composicaoSelecionada.setComponentes(componentes);
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}
	
	public String getDescricaoFormula() {
		return descricaoFormula;
	}

	public void setDescricaoFormula(String descricaoFormula) {
		this.descricaoFormula = descricaoFormula;
	}

	public Short getFnpSeq() {
		return fnpSeq;
	}

	public void setFnpSeq(Short fnpSeq) {
		this.fnpSeq = fnpSeq;
	}

	public ItemPrescricaoMedicaVO getPrescricaoNptItem() {
		return prescricaoNptItem;
	}

	public void setPrescricaoNptItem(ItemPrescricaoMedicaVO prescricaoNptItem) {
		this.prescricaoNptItem = prescricaoNptItem;
	}


	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public DominioTipoMedicaoPeso getTipoMedicao() {
		return tipoMedicao;
	}

	public void setTipoMedicao(DominioTipoMedicaoPeso tipoMedicao) {
		this.tipoMedicao = tipoMedicao;
	}

	public DadosPesoAlturaVO getDadosPesoAlturaVO() {
		return dadosPesoAlturaVO;
	}

	public void setDadosPesoAlturaVO(DadosPesoAlturaVO dadosPesoAlturaVO) {
		this.dadosPesoAlturaVO = dadosPesoAlturaVO;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getAltura() {
		return altura;
	}

	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public BigDecimal getSc() {
		return sc;
	}

	public void setSc(BigDecimal sc) {
		this.sc = sc;
	}

	public MpmParamCalculoPrescricao getParametroCalculo() {
		return parametroCalculo;
	}

	public void setParametroCalculo(MpmParamCalculoPrescricao parametroCalculo) {
		this.parametroCalculo = parametroCalculo;
	}
	

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public List<ItemPrescricaoMedicaVO> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<ItemPrescricaoMedicaVO> listaChecked) {
		this.listaChecked = listaChecked;
	}

}