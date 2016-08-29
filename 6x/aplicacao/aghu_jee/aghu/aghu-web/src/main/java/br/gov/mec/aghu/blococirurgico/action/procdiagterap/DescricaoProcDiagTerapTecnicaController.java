package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescPadrao;
import br.gov.mec.aghu.model.PdtDescTecnica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class DescricaoProcDiagTerapTecnicaController extends ActionController {

	private static final String SEQ_ESPECIALIDADE = "seqEspecialidade";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapTecnicaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3214269137124833456L;

	private Set<Object> nosExpandidos = new HashSet<Object>();

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	// Filtros
	private Date dthrInicioProcedimento;
	private Date dthrFimProcedimento;

	private List<NodoPOLVO> nodos = null;
	private AghEspecialidades especialidade;
	private String strDescricaoTecnica;
	private String strDescricaoTecnicaOld;
	private PdtDescricao descricao;
	private PdtDescTecnica descricaoTecnica;
	private PdtDadoDesc dadoDesc;
	
	private List<PdtProc> listaProc;
	private Integer ddtSeq;
	
	//ICONES UTILIZADOS
	private static final String IMAGES_ICONS_POR_ESPECIALIDADES_MEDICAS = "/resources/img/icons/especialidades-medicas-1.png";
	private static final String IMAGES_ICONS_ESPECIALIDADES_MEDICAS = "/resources/img/icons/especialidades-medicas-2.png";
	private static final String IMAGES_ICONS_PROCEDIMENTO = "/resources/img/icons/cirurgias.png";
	private static final String IMAGES_ICONS_PROCEDIMENTO_DESCRICAO = "/resources/img/icons/cirurgia_com_descricao.png";
	private static final String IMAGES_ICONS_POR_PROCEDIMENTO= "/resources/img/icons/escala_cirurgias.png";
	
    private static final String NOME_POR_PROCEDIMENTO = "POR_PROCEDIMENTO";
    private static final String NOME_POR_ESPECIALIDADE = "POR_ESPECIALIDADE";
    private static final String NOME_PROCEDIMENTO = "PROCEDIMENTO";
    private static final String NOME_ESPECIALIDADE = "ESPECIALIDADE";
    private static final String SELECIONAVEL = "SELECIONAVEL";
	
	private DescricaoProcDiagTerapVO descricaoProcDiagTerapVO;

	private Boolean ocorreuErroValidacaoDatas = Boolean.FALSE;

	private TreeNode nodoRaiz;
	private TreeNode nodoSelecionado;
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;

	public enum LiberacaoLaudoPreliminarONExceptionCode implements BusinessExceptionCode {
		MBC_01096
	}
	
	public void iniciar(DescricaoProcDiagTerapVO descricaoProcDiagTerapVO, PdtDescricao descricao) {
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
		
		ddtSeq = descricaoProcDiagTerapVO.getDdtSeq();
		especialidade = this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(descricaoProcDiagTerapVO.getEspSeqCirurgia());
		
		nodoRaiz = new DefaultTreeNode();

		listaProc = this.blocoCirurgicoProcDiagTerapFacade.pesquisarProcComProcedimentoCirurgicoAtivoPorDdtSeq(ddtSeq);
		if (!listaProc.isEmpty()) {
			for (PdtProc proc : listaProc) {
				MbcProcedimentoCirurgicos procedimentoCirurgico = proc.getPdtProcDiagTerap().getProcedimentoCirurgico();
				if (procedimentoCirurgico != null) {
					List<PdtDescPadrao> listaDescPadrao = this.blocoCirurgicoProcDiagTerapFacade.pesquisarDescPadraoProcedimentoCirurgicoAtivoPorEspSeqEPciSeq(
							especialidade.getSeq(), procedimentoCirurgico.getSeq());
					if (!listaDescPadrao.isEmpty()) {
						DefaultTreeNode nodoProcedimento = new DefaultTreeNode(new NodoPOLVO(0, NOME_POR_PROCEDIMENTO, "POR PROCEDIMENTO", IMAGES_ICONS_POR_PROCEDIMENTO, null, null), nodoRaiz);
						new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoProcedimento);
						break;
					}
				}
			}
		}

		DefaultTreeNode nodoEspecialidade = new DefaultTreeNode(new NodoPOLVO(0, NOME_POR_ESPECIALIDADE, "POR ESPECIALIDADE", IMAGES_ICONS_POR_ESPECIALIDADES_MEDICAS, null, null), nodoRaiz);
		new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoEspecialidade);

		this.descricao = descricao;
		
		strDescricaoTecnica = null;
		
		dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(ddtSeq);
		
		carregarDescricaoTecnica();
		
		carregarCamposDataHoraProcedimento();
	}

	public void onNodeExpand(NodeExpandEvent event) {  
        TreeNode nodo = event.getTreeNode();
        
        if (nodo == null) {
			return;
		}	

        nodo.getChildren().clear();
        NodoPOLVO no = (NodoPOLVO)nodo.getData();
        
        if (no.getTipo().equals(NOME_POR_ESPECIALIDADE)){
			for(AghEspecialidades especialidade : this.blocoCirurgicoProcDiagTerapFacade.pesquisarEspecialidadeDescricaoPadraoProcCirurgicoAtivo()) {
				NodoPOLVO nodoVO = new NodoPOLVO(0,NOME_ESPECIALIDADE, especialidade.getSigla(), IMAGES_ICONS_ESPECIALIDADES_MEDICAS , null, null);
				nodoVO.addParam(SEQ_ESPECIALIDADE, especialidade.getSeq());
				DefaultTreeNode nodoEspecialidade = new DefaultTreeNode(nodoVO, nodo);
				new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoEspecialidade);
			}
        } else if (no.getTipo().equals(NOME_POR_PROCEDIMENTO)) {				
			for (PdtProc proc : listaProc) {
				MbcProcedimentoCirurgicos procedimentoCirurgico = proc.getPdtProcDiagTerap().getProcedimentoCirurgico();
				if (procedimentoCirurgico != null) {
					for (PdtDescPadrao descPadrao : this.blocoCirurgicoProcDiagTerapFacade.pesquisarDescPadraoProcedimentoCirurgicoAtivoPorEspSeqEPciSeq(especialidade.getSeq(), procedimentoCirurgico.getSeq())) {						
						MbcProcedimentoCirurgicos procCrgDescPadrao = descPadrao.getPdtProcDiagTerap().getProcedimentoCirurgico();
						if (procCrgDescPadrao != null) {
							NodoPOLVO nodoVO = new NodoPOLVO(0,NOME_PROCEDIMENTO, procCrgDescPadrao.getDescricao() + " ("+especialidade.getSigla()+")", IMAGES_ICONS_PROCEDIMENTO, null, null);
							nodoVO.addParam(SEQ_ESPECIALIDADE, descPadrao.getId().getEspSeq());
							nodoVO.addParam("seqProcedimento", procCrgDescPadrao.getSeq());
							nodoVO.addParam("vlrBoolean", false);
							DefaultTreeNode nodoProcedimento = new DefaultTreeNode(nodoVO, nodo);
							new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoProcedimento);
							break;
						}
					}
				}
			}
        } else if (no.getTipo().equals(NOME_ESPECIALIDADE)) {
        	for(MbcProcedimentoCirurgicos procedimento : this.blocoCirurgicoProcDiagTerapFacade.pesquisarProcCirurgicoAtivoDescricaoPadraoPorEspSeq((Short)no.getParametros().get(SEQ_ESPECIALIDADE))) {
        		if (procedimento != null) {
					NodoPOLVO nodoVO = new NodoPOLVO(0,NOME_PROCEDIMENTO, procedimento.getDescricao(), IMAGES_ICONS_PROCEDIMENTO, null, null);
					nodoVO.addParam(SEQ_ESPECIALIDADE, (Short) no.getParametros().get(SEQ_ESPECIALIDADE));
					nodoVO.addParam("seqProcedimento", procedimento.getSeq());
					DefaultTreeNode nodoNomeProcedimento = new DefaultTreeNode(nodoVO, nodo);
					new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoNomeProcedimento);
        		}
			}
		} else if (no.getTipo().equals(NOME_PROCEDIMENTO)) {
			Short espSeq = (Short) no.getParametros().get(SEQ_ESPECIALIDADE);
			Integer procSeq	= (Integer) no.getParametros().get("seqProcedimento");	
			for(PdtDescPadrao titulo : this.blocoCirurgicoProcDiagTerapFacade.pesquisarDescPadraoProcedimentoCirurgicoAtivoPorEspSeqEPciSeq(espSeq, procSeq)) {
				NodoPOLVO nodoVO = new NodoPOLVO(0,SELECIONAVEL, titulo.getTitulo(), IMAGES_ICONS_PROCEDIMENTO_DESCRICAO , null, null);
				nodoVO.addParam("tituloDescTecPadrao", titulo.getTitulo());
				nodoVO.addParam("descricao", titulo.getDescTecPadrao());
				new DefaultTreeNode(nodoVO, nodo);
			}
		}
	}

	public void onNodeSelect(NodeSelectEvent event){
		TreeNode nodo = event.getTreeNode();
		if (nodo == null) {
			return;
		}	
		
		NodoPOLVO no = (NodoPOLVO)nodo.getData();
		if (no.getTipo().equals(SELECIONAVEL)){	        
			carregarDescricao((NodoPOLVO) nodo.getData());
			gravarDescricaoTecnica();
		}      
	}

	public void carregarDescricao(NodoPOLVO nodo) {
		String descricaoTec = (String) nodo.getParametros().get("descricao");
		if (strDescricaoTecnica != null){
			strDescricaoTecnica = strDescricaoTecnica+"\n"+ descricaoTec;
		}
		else {
			strDescricaoTecnica = descricaoTec;
		}
	}

	private void carregarCamposDataHoraProcedimento() {
		if (dadoDesc != null && !ocorreuErroValidacaoDatas) {
			dthrInicioProcedimento = dadoDesc.getDthrInicio();
			dthrFimProcedimento = dadoDesc.getDthrFim();
		}
	}
	
	public Boolean validarDatasDadoDesc() {
		
		try {
			blocoCirurgicoProcDiagTerapFacade.validarDatasDadoDesc(dthrInicioProcedimento, dthrFimProcedimento, descricaoProcDiagTerapVO);	
		} catch (ApplicationBusinessException e) {
			
			if (e.getCode().toString().equalsIgnoreCase("MSG_FIM_CRG_MENOR_DATA_ATUAL")) {
				RequestContext.getCurrentInstance().execute("document.getElementById('dthrFimProcedimento:dthrFimProcedimento:inputId_input').focus();");
			}
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			ocorreuErroValidacaoDatas = Boolean.TRUE;
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	public boolean ultrapassaTempoMinimoCirurgia() {
		return blocoCirurgicoProcDiagTerapFacade.ultrapassaTempoMinimoCirurgia(dthrInicioProcedimento, dthrFimProcedimento, descricaoProcDiagTerapVO);	
	}
	
	public void gravarDadoDescDthrInicio() {
		RapServidores servidorLogado = null;
		
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			if (dadoDesc != null) {
				dadoDesc.setDthrInicio(dthrInicioProcedimento);
				// Retorna caso tenha ocorrido algum erro que não seja o de validação de tempo mínimo
				if (!validarDatasDadoDesc()) {
					return;
				}				
				dadoDesc = blocoCirurgicoProcDiagTerapFacade.atualizarDadoDesc(dadoDesc);
				//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_HORA_INICIO_PROCEDIMENTO_DESCRICAO_PROC_DIAG_TERAP");
			} else {
				dadoDesc = new PdtDadoDesc();
				dadoDesc.setDthrInicio(dthrInicioProcedimento);
				dadoDesc.setPdtDescricao(descricao);
				dadoDesc.setRapServidores(servidorLogado);
				// Retorna caso tenha ocorrido algum erro que não seja o de validação de tempo mínimo
				if (!validarDatasDadoDesc()) {
					return;
				}
				blocoCirurgicoProcDiagTerapFacade.inserirDadoDesc(dadoDesc);
				dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(dadoDesc.getSeq());
				//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_HORA_INICIO_PROCEDIMENTO_DESCRICAO_PROC_DIAG_TERAP");				
			}
			ocorreuErroValidacaoDatas = Boolean.FALSE;
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		} catch(Exception e){
			if (dadoDesc != null && dadoDesc.getSeq() != null) {
				dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(dadoDesc.getSeq());
			} else {
				dadoDesc = null;
			}
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			context.addMessage("Messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void gravarDadoDescDthrFim() {
		RapServidores servidorLogado = null;
		
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			if (dadoDesc != null) {
				dadoDesc.setDthrFim(dthrFimProcedimento);
				// Retorna caso tenha ocorrido algum erro que não seja o de validação de tempo mínimo
				if (!validarDatasDadoDesc()) {
					return;
				}				
				dadoDesc = blocoCirurgicoProcDiagTerapFacade.atualizarDadoDesc(dadoDesc);
				//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_HORA_FIM_PROCEDIMENTO_DESCRICAO_PROC_DIAG_TERAP");				
			} else {
				dadoDesc = new PdtDadoDesc();
				dadoDesc.setDthrFim(dthrFimProcedimento);
				dadoDesc.setPdtDescricao(descricao);
				dadoDesc.setRapServidores(servidorLogado);
				// Retorna caso tenha ocorrido algum erro que não seja o de validação de tempo mínimo
				if (!validarDatasDadoDesc()) {
					return;
				}				
				blocoCirurgicoProcDiagTerapFacade.inserirDadoDesc(dadoDesc);
				dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(dadoDesc.getSeq());
				//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_HORA_FIM_PROCEDIMENTO_DESCRICAO_PROC_DIAG_TERAP");				
			}
			ocorreuErroValidacaoDatas = Boolean.FALSE;
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		} catch(Exception e){
			if (dadoDesc != null && dadoDesc.getSeq() != null) {
				dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(dadoDesc.getSeq());
			} else {
				dadoDesc = null;
			}
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			context.addMessage("Messages", new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void gravarDescricaoTecnica() {
		this.validarDatasDadoDesc();
		if (descricaoTecnica == null) {
			carregarDescricaoTecnica();
		}
		
		if (StringUtils.isNotEmpty(strDescricaoTecnica) && !strDescricaoTecnica.equals(strDescricaoTecnicaOld)) {
			try {
				
				strDescricaoTecnica = strDescricaoTecnica.replaceAll("\\r\\n", "\n");
				
				descricaoTecnica.setDdtSeq(ddtSeq);
				descricaoTecnica.setPdtDescricao(descricao);
				descricaoTecnica.setDescricao(strDescricaoTecnica);
				this.blocoCirurgicoProcDiagTerapFacade.persistirPdtDescTecnica(descricaoTecnica);
				strDescricaoTecnicaOld = descricaoTecnica.getDescricao();
				//apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_DESCRICAO_PROC_DIAG_TERAP");
				descricaoTecnica = this.blocoCirurgicoProcDiagTerapFacade.obterDescricaoTecnicaPorChavePrimaria(descricaoTecnica.getSeq());
				relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}			
		}
	}
	
	public void limparDescricaoTecnica() {
		try {
			if (descricaoTecnica != null) {
				if(descricaoTecnica.getSeq() != null) {
					this.blocoCirurgicoProcDiagTerapFacade.excluirPdtDescTecnica(descricaoTecnica);
				}
				strDescricaoTecnica = null;
				this.descricaoTecnica = null;
			}
			this.strDescricaoTecnica = null;
			//apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_DESCRICAO_PROC_DIAG_TERAP");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	private void carregarDescricaoTecnica() {
		descricaoTecnica = blocoCirurgicoProcDiagTerapFacade.obterDescricaoTecnicaPorChavePrimaria(ddtSeq);
		if (descricaoTecnica == null) {
			descricaoTecnica = new PdtDescTecnica();
			descricaoTecnica.setPdtDescricao(descricao);
			strDescricaoTecnicaOld = null;
		} else {
			strDescricaoTecnica = descricaoTecnica.getDescricao();
			strDescricaoTecnicaOld = descricaoTecnica.getDescricao();
		}
	}
	
	public Set<Object> getNosExpandidos() {
		return nosExpandidos;
	}

	public void setNosExpandidos(Set<Object> nosExpandidos) {
		this.nosExpandidos = nosExpandidos;
	}

	public List<NodoPOLVO> getNodos() {
		return nodos;
	}

	public void setNodos(List<NodoPOLVO> nodos) {
		this.nodos = nodos;
	}

	public String getStrDescricaoTecnica() {
		return strDescricaoTecnica;
	}

	public void setStrDescricaoTecnica(String strDescricaoTecnica) {
		this.strDescricaoTecnica = strDescricaoTecnica;
	}

	public PdtDescricao getDescricao() {
		return descricao;
	}

	public void setDescricao(PdtDescricao descricao) {
		this.descricao = descricao;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Date getDthrInicioProcedimento() {
		return dthrInicioProcedimento;
	}

	public void setDthrInicioProcedimento(Date dthrInicioProcedimento) {
		this.dthrInicioProcedimento = dthrInicioProcedimento;
	}

	public Date getDthrFimProcedimento() {
		return dthrFimProcedimento;
	}

	public void setDthrFimProcedimento(Date dthrFimProcedimento) {
		this.dthrFimProcedimento = dthrFimProcedimento;
	}

	public Boolean getOcorreuErroValidacaoDatas() {
		return ocorreuErroValidacaoDatas;
	}

	public void setOcorreuErroValidacaoDatas(Boolean ocorreuErroValidacaoDatas) {
		this.ocorreuErroValidacaoDatas = ocorreuErroValidacaoDatas;
	}

	public TreeNode getNodoRaiz() {
		return nodoRaiz;
	}

	public void setNodoRaiz(TreeNode nodoRaiz) {
		this.nodoRaiz = nodoRaiz;
	}

	public TreeNode getNodoSelecionado() {
		return nodoSelecionado;
	}

	public void setNodoSelecionado(TreeNode nodoSelecionado) {
		this.nodoSelecionado = nodoSelecionado;
	}
}
