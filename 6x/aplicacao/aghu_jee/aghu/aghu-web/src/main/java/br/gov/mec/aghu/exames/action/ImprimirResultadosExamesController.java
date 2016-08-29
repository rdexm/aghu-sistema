package br.gov.mec.aghu.exames.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Protocolo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.DetalharItemSolicitacaoPacienteVO;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.exames.vo.ListaDesenhosMascarasExamesVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicConsultadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


@Deprecated
public class ImprimirResultadosExamesController extends ActionController{


	private static final String PREVIA_DESENHO_MASCARA_EXAME_VO = "previaDesenhoMascaraExameVO";

	private static final long serialVersionUID = 2941688789194461043L;

	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private  IExamesLaudosFacade examesLaudosFacade;	
	
	@Inject
	private RelatorioMascaraExamesController relatorioMascaraExamesController;

	private Map<Integer, Vector<Short>> solicitacoes;
	private DominioSubTipoImpressaoLaudo subTipoImpressao;
	private List<ListaDesenhosMascarasExamesVO> listaDesenhosMascarasExamesVO;
	private boolean isPrevia = false;
	private DesenhoMascaraExameVO previaDesenhoMascaraExameVO;
	private AelpCabecalhoLaudoVO auxCabecalhoLaudo;
	private UIComponent assinaturaEletronica;
	private Boolean isHist = Boolean.FALSE;
	private String paramSoeSeq;
	private String paramSubTipoImpressao;
	private String paramGrupoSeq;
	DetalharItemSolicitacaoPacienteVO pacienteVO = new DetalharItemSolicitacaoPacienteVO();
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void inicio() {
		try{
			listaDesenhosMascarasExamesVO = new ArrayList<ListaDesenhosMascarasExamesVO>();
			HttpSession session = null;
			HttpServletRequest request = null;
			boolean isToken = paramSoeSeq != null && paramSubTipoImpressao != null && paramGrupoSeq != null;
			if (isToken) {
				solicitacoes = new HashMap<Integer, Vector<Short>>();
				List<AelItemSolicitacaoExames> itensSolicitacaoExames = this.solicitacaoExameFacade.buscarItemExamesLiberadosPorGrupo(
								Integer.parseInt(paramSoeSeq),
								Integer.parseInt(paramGrupoSeq));
				this.solicitacoes.put(Integer.parseInt(paramSoeSeq), new Vector<Short>());
				for(AelItemSolicitacaoExames itemSolicitacaoExames : itensSolicitacaoExames){										
					this.solicitacoes.get(Integer.parseInt(paramSoeSeq)).add(itemSolicitacaoExames.getId().getSeqp());					
				}
				
			} else {
			
				request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
				session = request.getSession();

					if((solicitacoes == null || solicitacoes.isEmpty()) && session.getAttribute(PREVIA_DESENHO_MASCARA_EXAME_VO)!=null){
						isPrevia = true;
						previaDesenhoMascaraExameVO = (DesenhoMascaraExameVO)session.getAttribute(PREVIA_DESENHO_MASCARA_EXAME_VO);
					}
			}
			
			examesLaudosFacade.verificaLaudoPatologia(solicitacoes, this.isHist);
			inicializa(isToken, session);
			
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void inicializa(boolean isToken, HttpSession session) throws BaseException {
		if (!isPrevia) {
			montaRegistroNaoPrevia(isToken, session);
		}
		else {
			montaRegistroPrevia(isToken, session);
		}
		
	}
	@SuppressWarnings("PMD.NPathComplexity")
	private void montaRegistroNaoPrevia(boolean isToken, HttpSession session) throws BaseException {
		/*Limpa os dados do cabeçalho*/
		auxCabecalhoLaudo=null;
		
		Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
		while (it.hasNext()) {
			 Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();

			 Integer solicitacao = paramCLValores.getKey(); 
			 List<Short> seqps = relatorioMascaraExamesController.ordenarExames(solicitacao, paramCLValores.getValue());

			 Collections.sort(seqps);
			 for (Iterator iterator = seqps.iterator(); iterator.hasNext();) {
				montaVO((Short) iterator.next(), isToken, solicitacao, !iterator.hasNext(), !iterator.hasNext() ? seqps : null);
			}

			AelSolicitacaoExames     solic = null;
			AelSolicitacaoExamesHist solicHist = null;
			
			if(isHist){
				solicHist = this.examesFacade.obterAelSolicitacaoExameHistPorChavePrimaria(relatorioMascaraExamesController.getSolicitacaoExameSeq(),
						AelSolicitacaoExamesHist.Fields.ATENDIMENTO);
			}else{
				solic = this.examesFacade.obterAelSolicitacaoExamePorChavePrimaria(relatorioMascaraExamesController.getSolicitacaoExameSeq(),
						AelSolicitacaoExames.Fields.ATENDIMENTO);
			}

			pacienteVO = new DetalharItemSolicitacaoPacienteVO();
			DominioOrigemAtendimento origem = null;

			if(isHist){
				if(solicHist.getRecemNascido()){
					pacienteVO.setNomePaciente("RN de " + examesFacade.buscarLaudoNomeMaeRecemNascidoHist(solicHist));
				}else{
					pacienteVO.setNomePaciente(examesFacade.buscarLaudoNomePacienteHist(solicHist) );
				}
				pacienteVO.setProntuarioPaciente(examesFacade.buscarLaudoProntuarioPacienteHist(solicHist));
				if(solicHist != null 
						&& solicHist.getAtendimento() != null 
						&& solicHist.getAtendimento().getLeito() != null){
						pacienteVO.setLeito(solicHist.getAtendimento().getLeito());
				}
				origem = pesquisaExamesFacade.validaLaudoOrigemPacienteHist(solicHist);
			}else{
				if(solic.getRecemNascido()){
					pacienteVO.setNomePaciente("RN de " + examesFacade.buscarLaudoNomeMaeRecemNascido(solic));
				}else{
					pacienteVO.setNomePaciente(examesFacade.buscarLaudoNomePaciente(solic) );
				}
				pacienteVO.setProntuarioPaciente(examesFacade.buscarLaudoProntuarioPaciente(solic));
				if(solic != null 
						&& solic.getAtendimento() != null 
						&& solic.getAtendimento().getLeito() != null){
						pacienteVO.setLeito(solic.getAtendimento().getLeito());
				}
				origem = pesquisaExamesFacade.validaLaudoOrigemPaciente(solic);
			}
			
			if(origem != null) {
				pacienteVO.setOrigem(origem.getDescricao());		
			}
			
			if (!isToken) {
			   pacienteVO.setUsuarioLogado(registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado()).getPessoaFisica().getNome());
			}
			
			for (ListaDesenhosMascarasExamesVO vo : listaDesenhosMascarasExamesVO) {
				if (vo.getPacienteVO() == null) {
					vo.setPacienteVO(pacienteVO);
				}
			}
		}

		/*Remove da sessão*/
		if (!isToken) {
			session.removeAttribute("solicitacoesImprimir");
			session.removeAttribute(PREVIA_DESENHO_MASCARA_EXAME_VO);
		}

		solicitacoes=null;
		isPrevia=false;
	}
	
	private void montaVO(Short seqp, boolean isToken, Integer solicitacao, boolean ultimoItem, List<Short> seqps) throws BaseException {
		relatorioMascaraExamesController.setIsHist(isHist);
		relatorioMascaraExamesController.setSolicitacaoExameSeq(solicitacao);
		relatorioMascaraExamesController.setItemSolicitacaoExameSeq(seqp);
		relatorioMascaraExamesController.setIsUltimoItem(ultimoItem);
		relatorioMascaraExamesController.setSeqps(seqps);
		relatorioMascaraExamesController.inicializar(true, subTipoImpressao);	

		List<DesenhoMascaraExameVO> desenhosMascarasExamesVO = relatorioMascaraExamesController.getDesenhosMascarasExamesVO();
		
		if (auxCabecalhoLaudo == null && !desenhosMascarasExamesVO.isEmpty()) {
			auxCabecalhoLaudo = desenhosMascarasExamesVO.get(0).getCabecalhoLaudo();
		}
		
		if (existeDesenhoMascaraExames(desenhosMascarasExamesVO)) {
			for (DesenhoMascaraExameVO des : desenhosMascarasExamesVO) {
				if (des.isQuebrarPagina() || cabecalhoLaudoDescricaoUnidadeVazio(des)) {
					auxCabecalhoLaudo = des.getCabecalhoLaudo();
					des.setQuebrarPagina(Boolean.TRUE);
				}
			}
		}

		ListaDesenhosMascarasExamesVO vo = new ListaDesenhosMascarasExamesVO();
		if (existeDesenhoMascaraExames(desenhosMascarasExamesVO)) {
			vo.setDesenhosMascarasExamesVO(desenhosMascarasExamesVO);
		}

		List<LinhaReportVO> notasAdicionais = examesFacade.pesquisarNotaAdicionalPorSolicitacaoEItemVO(solicitacao,seqp, isHist);
		vo.setNotasAdicionais(notasAdicionais);
		vo.setSoeSeq(solicitacao);
		vo.setIseSeqp(seqp);

		/*Insere visualização do item*/
		AelItemSolicConsultado itemSolicConsultado = new AelItemSolicConsultado();
		AelItemSolicConsultadoId id = new AelItemSolicConsultadoId();
		id.setIseSeqp(seqp);
		id.setIseSoeSeq(solicitacao);
		itemSolicConsultado.setId(id);
		
		if (!isToken) {
			examesFacade.insereVisualizacaoItemSolicitacao(itemSolicConsultado, Boolean.FALSE);
		}
		
		listaDesenhosMascarasExamesVO.add(vo);
	}


	private Boolean existeDesenhoMascaraExames(List<DesenhoMascaraExameVO> desenhosMascarasExamesVO) {
		return desenhosMascarasExamesVO != null && !desenhosMascarasExamesVO.isEmpty();
	}
	
	private Boolean cabecalhoLaudoDescricaoUnidadeVazio(DesenhoMascaraExameVO des) {
		String tipoDescUnidade = des.getCabecalhoLaudo().getpDescUnidade();
		return StringUtils.isNotBlank(tipoDescUnidade) && StringUtils.isNotBlank(auxCabecalhoLaudo.getpDescUnidade())
				&& !tipoDescUnidade.equals(auxCabecalhoLaudo.getpDescUnidade());
	}

	private void montaRegistroPrevia(boolean isToken, HttpSession session) {
		/*Limpa os dados do cabeçalho*/
		auxCabecalhoLaudo=null;

		/*Se for prévia*/
		ListaDesenhosMascarasExamesVO vo = new ListaDesenhosMascarasExamesVO();
		vo.getDesenhosMascarasExamesVO().add(previaDesenhoMascaraExameVO);

		List<LinhaReportVO> notasAdicionais = new ArrayList<LinhaReportVO>();

		for (int i = 0; i < 3; i++) {
			LinhaReportVO notaAdicional = new LinhaReportVO();
			notaAdicional.setTexto1("Teste prévia nota adicional " + i+1);
			notaAdicional.setData(new Date());
			//notaAdicional.setServidor(new RapServidores(null, new RapPessoasFisicas(), null));
			notaAdicional.setTexto2("Nome Pessoa Servidor " + i+1);
			notasAdicionais.add(notaAdicional);
		}

		vo.setNotasAdicionais(notasAdicionais);
		vo.setSoeSeq(12345678);
		vo.setIseSeqp((short)10);

		pacienteVO = new DetalharItemSolicitacaoPacienteVO();
		pacienteVO.setNomePaciente("Nome paciente prévia");
		pacienteVO.setProntuarioPaciente("1234567/8");
		AinLeitos leito = new AinLeitos();
		leito.setLeitoID("12345");
		pacienteVO.setLeito(leito);

		/*Remove da sessão*/
		session.removeAttribute(PREVIA_DESENHO_MASCARA_EXAME_VO);
		session.removeAttribute("solicitacoesImprimir");

		listaDesenhosMascarasExamesVO.add(vo);
	}

	public String recuperaCaminhoLogo() {
		return parametroFacade.recuperarCaminhoLogo().replace("/aghu.war", "");
	}

	public Map<Integer, Vector<Short>> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Map<Integer, Vector<Short>> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public List<ListaDesenhosMascarasExamesVO> getListaDesenhosMascarasExamesVO() {
		return listaDesenhosMascarasExamesVO;
	}

	public void setListaDesenhosMascarasExamesVO(List<ListaDesenhosMascarasExamesVO> listaDesenhosMascarasExamesVO) {
		this.listaDesenhosMascarasExamesVO = listaDesenhosMascarasExamesVO;
	}

	public AelpCabecalhoLaudoVO getCabecalhoLaudo() {
		return auxCabecalhoLaudo;
	}

	public void setCabecalhoLaudo(AelpCabecalhoLaudoVO cabecalhoLaudo) {
		this.auxCabecalhoLaudo = cabecalhoLaudo;
	}

	public UIComponent getAssinaturaEletronica() {
		return assinaturaEletronica;
	}

	public void setAssinaturaEletronica(UIComponent assinaturaEletronica) {
		this.assinaturaEletronica = assinaturaEletronica;
	}

	public DominioSubTipoImpressaoLaudo getSubTipoImpressao() {
		return subTipoImpressao;
	}

	public void setSubTipoImpressao(DominioSubTipoImpressaoLaudo subTipoImpressao) {
		this.subTipoImpressao = subTipoImpressao;
	}
	public String getParamSoeSeq() {
		return paramSoeSeq;
	}

	public void setParamSoeSeq(String paramSoeSeq) {
		this.paramSoeSeq = paramSoeSeq;
	}

	public String getParamGrupoSeq() {
		return paramGrupoSeq;
	}

	public void setParamGrupoSeq(String paramGrupoSeq) {
		this.paramGrupoSeq = paramGrupoSeq;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}
	
	public String getLogo() throws ApplicationBusinessException{
		String caminhoLogo = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGOTIPO_LAUDO_EXAMES); 
		if(new File(caminhoLogo).exists()){
			return caminhoLogo;
		} else {
			StringBuilder caminhoDoContexto = this.caminhoDoContexto();
			return caminhoDoContexto.append(caminhoLogo).toString();
		}
	}
	public StringBuilder caminhoDoContexto() throws ApplicationBusinessException{
		HttpServletRequest request = null;
			
		String servidor = request.getServerName();
		String contexto = request.getContextPath();
		Aplicacao aplicacao = cascaFacade.obterAplicacaoPorContexto(contexto, servidor);
		String protocolo = aplicacao.getProtocolo() != null ? aplicacao.getProtocolo().getDescricao() : Protocolo.HTTPS.getDescricao();
		Integer porta = aplicacao.getPorta() != null ? aplicacao.getPorta() : 80;
			
		StringBuilder baseUrl = new StringBuilder(protocolo).append("://").append(servidor);
			
		if(porta != 80) {
			baseUrl.append(':').append(porta);
		}
			
		baseUrl.append(contexto);
			
		return baseUrl;
		}
	
	public DetalharItemSolicitacaoPacienteVO getPacienteVO() {
		return pacienteVO;
	}

	public void setPacienteVO(DetalharItemSolicitacaoPacienteVO pacienteVO) {
		this.pacienteVO = pacienteVO;
	}
}

