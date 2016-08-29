package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoTecnicas;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoCirurgicaTecnicaController  extends ActionController {

	private static final String SEQ_ESPECIALIDADE = "seqEspecialidade";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5078428678012061717L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	
	private List<NodoPOLVO> nodos = null;
	private AghEspecialidades especialidade;
	private String descricaoTecnica;
	private String descricaoTecnicaOld;
	private MbcDescricaoCirurgica descricaoCirurgica;
	private MbcDescricaoTecnicas descricao;
	
	private Boolean ocorreuEdicaoDescricao = false;
	private Boolean exibirModal = false;
	private Boolean gravacaoAutomatica = false;
	private DescricaoCirurgicaVO descricaoCirurgicaVO; 
	
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
    
	private TreeNode nodoRaiz;
	private TreeNode nodoSelecionado;
	
	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	public void iniciar(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.setDescricaoCirurgicaVO(descricaoCirurgicaVO);
		this.descricaoCirurgica = this.blocoCirurgicoFacade.buscarDescricaoCirurgica(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp());
	
		this.carregar();
	}
	
	public void carregar() {

		nodoRaiz = new DefaultTreeNode();
		DefaultTreeNode nodoEspecialidade = new DefaultTreeNode(new NodoPOLVO(0,NOME_POR_ESPECIALIDADE, "POR ESPECIALIDADE", IMAGES_ICONS_POR_ESPECIALIDADES_MEDICAS, null, null), nodoRaiz);
		new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoEspecialidade);

		List<MbcProcDescricoes> descricoes = this.blocoCirurgicoFacade.listarProcDescricoesComProcedimentoAtivo(descricaoCirurgica.getId().getCrgSeq(), descricaoCirurgica.getId().getSeqp());
		if(descricoes != null && !descricoes.isEmpty()) {
			for(MbcProcDescricoes descricao : descricoes) {
				List<MbcDescricaoPadrao> padroes = this.blocoCirurgicoFacade.buscarDescricaoPadraoPelaEspecialidadeEProcedimento(especialidade.getSeq(), descricao.getProcedimentoCirurgico().getSeq());
				if(padroes != null && !padroes.isEmpty()) {
					DefaultTreeNode nodoProcedimento = new DefaultTreeNode(new NodoPOLVO(0,NOME_POR_PROCEDIMENTO, "POR PROCEDIMENTO", IMAGES_ICONS_POR_PROCEDIMENTO , null, null), nodoRaiz);
					new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoProcedimento);
					break;
				}
			}
		}
		
		descricao = blocoCirurgicoFacade.buscarDescricaoTecnicas(descricaoCirurgica.getId().getCrgSeq(), descricaoCirurgica.getId().getSeqp());
		if (this.getDescricaoTecnica()==null 
				|| (this.getDescricaoTecnica()!=null &&  this.getDescricaoTecnica().length()<=4000)){
			descricao = blocoCirurgicoFacade.buscarDescricaoTecnicas(descricaoCirurgica.getId().getCrgSeq(), descricaoCirurgica.getId().getSeqp());

			if(descricao == null) {
				descricao = new MbcDescricaoTecnicas();
				descricao.setId(descricaoCirurgica.getId());
				descricao.setMbcDescricaoCirurgica(descricaoCirurgica);
				descricaoTecnica = "";
				descricaoTecnicaOld = null;
			}
			else {
				descricaoTecnica = descricao.getDescricaoTecnica();
				descricaoTecnicaOld = new String(descricao.getDescricaoTecnica());
				exibirModal = true;
			}
		}	

		this.gravacaoAutomatica = true;
		this.setOcorreuEdicaoDescricao(false);
	}
	
	public boolean validarCamposObrigatorios(){
		boolean validado = true;
		
		if(descricaoTecnica == null){
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_DESCRICAO_TECNICA_DESCRICAO");
			validado = false;			
		}
		
		return validado;
	}
	
	public void onNodeExpand(NodeExpandEvent event) {  
        TreeNode nodo = event.getTreeNode();
        
        if (nodo == null) {
			return;
		}	

        nodo.getChildren().clear();
        NodoPOLVO no = (NodoPOLVO)nodo.getData();
        
        if (no.getTipo().equals(NOME_POR_ESPECIALIDADE)){	        
			for(AghEspecialidades especialidade : this.blocoCirurgicoFacade.pGeraDadosEsp()) {
				NodoPOLVO nodoVO = new NodoPOLVO(0,NOME_ESPECIALIDADE, especialidade.getSigla(), IMAGES_ICONS_ESPECIALIDADES_MEDICAS , null, null);
				nodoVO.addParam(SEQ_ESPECIALIDADE, especialidade.getSeq());
				DefaultTreeNode nodoEspecialidade = new DefaultTreeNode(nodoVO, nodo);
				new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoEspecialidade);
			}
        } else if (no.getTipo().equals(NOME_POR_PROCEDIMENTO)) {				
			List<MbcProcDescricoes> descricoes = this.blocoCirurgicoFacade.listarProcDescricoesComProcedimentoAtivo(descricaoCirurgica.getId().getCrgSeq(), descricaoCirurgica.getId().getSeqp());
			for(MbcProcDescricoes descricao : descricoes) {
				for(MbcDescricaoPadrao padrao : this.blocoCirurgicoFacade.buscarDescricaoPadraoPelaEspecialidadeEProcedimento(especialidade.getSeq(), descricao.getProcedimentoCirurgico().getSeq())) {
					NodoPOLVO nodoVO = new NodoPOLVO(0,NOME_PROCEDIMENTO, padrao.getMbcProcedimentoCirurgicos().getDescricao() + " ("+especialidade.getSigla()+")", IMAGES_ICONS_PROCEDIMENTO, null, null);
					nodoVO.addParam(SEQ_ESPECIALIDADE, padrao.getId().getEspSeq());
					nodoVO.addParam("seqProcedimento", padrao.getMbcProcedimentoCirurgicos().getSeq());
					nodoVO.addParam("vlrBoolean", false);
					DefaultTreeNode nodoProcedimento = new DefaultTreeNode(nodoVO, nodo);
					new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoProcedimento);
					break;
				}
			}
        } else if (no.getTipo().equals(NOME_ESPECIALIDADE)) {
			for(MbcProcedimentoCirurgicos procedimento : this.blocoCirurgicoFacade.pGeraDadosProc((Short) no.getParametros().get(SEQ_ESPECIALIDADE))) {
				NodoPOLVO nodoVO = new NodoPOLVO(0,NOME_PROCEDIMENTO, procedimento.getDescricao(), IMAGES_ICONS_PROCEDIMENTO, null, null);
				nodoVO.addParam(SEQ_ESPECIALIDADE, (Short) no.getParametros().get(SEQ_ESPECIALIDADE));
				nodoVO.addParam("seqProcedimento", procedimento.getSeq());
				DefaultTreeNode nodoNomeProcedimento = new DefaultTreeNode(nodoVO, nodo);
				new DefaultTreeNode(new NodoPOLVO(0, null, null, null, null, null), nodoNomeProcedimento);
			}
		} else if (no.getTipo().equals(NOME_PROCEDIMENTO)) {
			Short espSeq = (Short) no.getParametros().get(SEQ_ESPECIALIDADE);
			Integer procSeq	= (Integer) no.getParametros().get("seqProcedimento");	
			for(MbcDescricaoPadrao titulo : this.blocoCirurgicoFacade.buscarDescricaoPadraoPelaEspecialidadeEProcedimento(espSeq,procSeq)) {
				NodoPOLVO nodoVO = new NodoPOLVO(0,SELECIONAVEL, titulo.getTitulo(), IMAGES_ICONS_PROCEDIMENTO_DESCRICAO , null, null);
				nodoVO.addParam("tituloDescTecPadrao", titulo.getDescricaoTecPadrao());
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
			this.gravarDescricaoTecnica();
		}      
	}
	
	public void desabilitarGravacaoAutomatica() {
		this.gravacaoAutomatica = false;
	}
	
	public void carregarDescricao(NodoPOLVO nodo) {
		String descricaoTec = (String) nodo.getParametros().get("tituloDescTecPadrao");
		if (descricaoTecnica != null){
			if ((descricaoTecnica.length()+1+descricaoTec.length())>4000){
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_DESCRICAO_TECNICA_MAIOR");
				return;
			}
			descricaoTecnica = descricaoTecnica+"\n"+ descricaoTec;
		}
		else {
			descricaoTecnica = descricaoTec;
		}
		this.ocorreuEdicaoDescricao = true;	 
		
		this.adicionarDescricaoPeloNodo();
	}
	
	
	/**
	 * Adiciona Previsao pelo Nodo
	 */
	public void adicionarDescricaoPeloNodo() {
		try {
			//concatenacao já ocorre no metodo que efetua a chamada
			descricaoTecnica = StringUtils.defaultString(descricaoTecnica);
			String novaDescricaoTecnica = descricaoTecnica;
			this.blocoCirurgicoFacade.validarTamanhoDescricaoTecnica(novaDescricaoTecnica);
			descricaoTecnica = novaDescricaoTecnica;
			descricao.setDescricaoTecnica(descricaoTecnica);
			this.blocoCirurgicoFacade.persistirDescricaoTecnica(descricao);			
			descricaoTecnicaOld = new String(descricao.getDescricaoTecnica());
			//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ADICIONAR_DESCRICAO_TECNICA");
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}

	/**
	 * Adiciona Descricao pelo digitar na area texto
	 */
	public void adicionarDescricao() {
		try {
			// Caso o usuário já tenha gravado uma descrição técnica e depois
			// apaque essa descrição técnica pelo TextArea
			// irá concatenar uma string vazia
			descricaoTecnica = StringUtils.defaultString(descricaoTecnica);
			
			this.blocoCirurgicoFacade.validarTamanhoDescricaoTecnica(descricaoTecnica);
			descricao.setDescricaoTecnica(descricaoTecnica);
			this.blocoCirurgicoFacade.persistirDescricaoTecnica(descricao);			
			descricaoTecnicaOld = new String(descricao.getDescricaoTecnica());
			//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ADICIONAR_DESCRICAO_TECNICA");
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarDescricaoTecnica() {
		
		if(descricaoTecnica == null){
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_DESCRICAO_TECNICA_DESCRICAO");
			return;
		}
		
		// Caso o usuário já tenha gravado uma descrição técnica e depois apaque
		// essa descrição técnica pelo TextArea
		if (descricao != null && descricao.getDescricaoTecnica() != null
				&& StringUtils.isEmpty(descricaoTecnica)) {
			limparDescricaoTecnica();
			return;
		}
		
		try {
			if (descricaoTecnica!=null){
				
				descricaoTecnica = descricaoTecnica.replaceAll("\\r\\n", "\n");
				
				descricao.setDescricaoTecnica(descricaoTecnica);
				
				if (this.descricaoTecnica!=null && this.descricaoTecnica.length()>4000){
					apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_DESCRICAO_TECNICA_MAIOR");
					return;
				}
				this.blocoCirurgicoFacade.persistirDescricaoTecnica(descricao);	
				//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_DESCRICAO_TECNICA");
			}	

			
			if (descricao != null && descricao.getDescricaoTecnica()!=null){
				descricaoTecnicaOld = new String(descricao.getDescricaoTecnica());
			}	

			this.setOcorreuEdicaoDescricao(false);
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void limparDescricaoTecnica() {
		try {
			if(exibirModal) {				
				this.blocoCirurgicoFacade.excluirDescricaoTecnica(descricao);
				
				this.descricaoTecnica = null;
				this.descricaoTecnicaOld = null;
				this.exibirModal = false;
				this.gravacaoAutomatica = true;
				this.ocorreuEdicaoDescricao = false;
				this.descricao = new MbcDescricaoTecnicas();
				this.descricao.setId(descricaoCirurgica.getId());
				this.descricao.setMbcDescricaoCirurgica(descricaoCirurgica);
			}
			else {
				this.descricaoTecnica = null;
				this.descricaoTecnicaOld = null;
			}
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_DESCRICAO_TECNICA_DESCRICAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
		
	}
	
	public Boolean validarGravacaoAutomatica() {
		if(StringUtils.isEmpty(descricaoTecnica) || CoreUtil.igual(descricaoTecnica, descricaoTecnicaOld)) {
			return false;
		}
		return gravacaoAutomatica;
	}

	public List<NodoPOLVO> getNodos() {
		return nodos;
	}

	public void setNodos(List<NodoPOLVO> nodos) {
		this.nodos = nodos;
	}

	public String getDescricaoTecnica() {
		return descricaoTecnica;
	}

	public void setDescricaoTecnica(String descricaoTecnica) {
		this.descricaoTecnica = descricaoTecnica;
	}

	public MbcDescricaoCirurgica getDescricaoCirurgica() {
		return descricaoCirurgica;
	}

	public void setDescricaoCirurgica(MbcDescricaoCirurgica descricaoCirurgica) {
		this.descricaoCirurgica = descricaoCirurgica;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public Boolean getGravacaoAutomatica() {
		return gravacaoAutomatica;
	}

	public void setGravacaoAutomatica(Boolean gravacaoAutomatica) {
		this.gravacaoAutomatica = gravacaoAutomatica;
	}

	public Boolean getOcorreuEdicaoDescricao() {
		return ocorreuEdicaoDescricao;
	}

	public void setOcorreuEdicaoDescricao(Boolean ocorreuEdicaoDescricao) {
		this.ocorreuEdicaoDescricao = ocorreuEdicaoDescricao;
	}
	
	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}

	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
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
