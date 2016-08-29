package br.gov.mec.aghu.exames.patologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioWorkflowExamePatologia;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.SecaoConfExameVO;
import br.gov.mec.aghu.model.AelSecaoConfExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCaracteristicaMaterialAnaliseController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterCaracteristicaMaterialAnaliseController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8443535432394440149L;

	private static final String TRES_PONTOS = "...";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Integer lu2Seq;
	private SecaoConfExameVO vo;
	private List<SecaoConfExameVO> lista;

	private Boolean mostraBotaoVoltar;

	public void inicio(){
	 

		if(lu2Seq != null){
			this.vo = new SecaoConfExameVO();
			this.lista = new ArrayList<SecaoConfExameVO>();
			this.vo.setConfigExameLaudoUnico(this.examesFacade.bucarConfigExameLaudoUnicoOriginal(this.lu2Seq));
			this.pesquisar();
		}
	
	}

	private void pesquisar(){
		Integer versaoConf = this.examesFacade.buscarMaxVersaoConfPorLu2Seq(lu2Seq);
		if(versaoConf != null){
			List<AelSecaoConfExames> listaSecConfEx = this.examesFacade.buscarPorLu2SeqEVersaoConf(this.lu2Seq, versaoConf);
			if(listaSecConfEx != null){
				this.bindLista(listaSecConfEx);
			}
		}
	}

	private void bindLista(List<AelSecaoConfExames> listaSecConfEx) {
		this.lista = new ArrayList<SecaoConfExameVO>();
		for(AelSecaoConfExames aelSecaoConfExames : listaSecConfEx){
			SecaoConfExameVO vo = new SecaoConfExameVO();
			vo.setSecaoConfExames(aelSecaoConfExames);
			if(DominioSituacao.A.equals(aelSecaoConfExames.getIndSituacao())){
				vo.setAtivo(Boolean.TRUE);
			} else {
				vo.setAtivo(Boolean.FALSE);
			}
			vo.setImpressao(aelSecaoConfExames.getIndImprimir());
			vo.setObrigatorio(aelSecaoConfExames.getIndObrigatorio());

			if(aelSecaoConfExames.getEtapaLaudo()!=null){
				if(aelSecaoConfExames.getEtapaLaudo().equals(DominioSituacaoExamePatologia.MC)){
					vo.setSecaoObrigatoria(DominioWorkflowExamePatologia.MC);
				}else if(aelSecaoConfExames.getEtapaLaudo().equals(DominioSituacaoExamePatologia.TC)){
					vo.setSecaoObrigatoria(DominioWorkflowExamePatologia.TC);
				}
			}


			this.lista.add(vo);
		}
	}

	public void salvar(){
		try {
			RapServidores colaborador = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			this.examesFacade.salvarSecaoConfExames(this.lista, this.lu2Seq, colaborador);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_MANTER_CARACTERISTICA_MATERIAL_ANALISE");
		}  catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			this.mostraBotaoVoltar = Boolean.FALSE;
			return;
		}
		this.mostraBotaoVoltar = Boolean.TRUE;
		this.inicio();
	}

	public boolean habilitaSecaoObrigatoria(boolean ativo, boolean obrigatorio){
		return this.examesFacade.habilitaSecaoObrigatoria(ativo, obrigatorio);
	}
	
	public String cancelar(){
		lu2Seq = null;
		return "exames-manterConfiguracaoExamesList";
	}

	public String getNomeLaudoUnicoTrucado(){
		if(this.vo != null && vo.getConfigExameLaudoUnico() != null && vo.getConfigExameLaudoUnico().getNome() != null){
			if(vo.getConfigExameLaudoUnico().getNome().length() > 100){
				return vo.getConfigExameLaudoUnico().getNome().substring(0, 99) + TRES_PONTOS;
			} else {
				return vo.getConfigExameLaudoUnico().getNome();
			}
		}
		return "";
	}

	// GETTERS E SETTERS	

	public SecaoConfExameVO getVo() {
		return vo;
	}

	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}

	public void setVo(SecaoConfExameVO vo) {
		this.vo = vo;
	}

	public List<SecaoConfExameVO> getLista() {
		return lista;
	}

	public void setLista(List<SecaoConfExameVO> lista) {
		this.lista = lista;
	}

	public Boolean getMostraBotaoVoltar() {
		return mostraBotaoVoltar;
	}

	public void setMostraBotaoVoltar(Boolean mostraBotaoVoltar) {
		this.mostraBotaoVoltar = mostraBotaoVoltar;
	}	
}