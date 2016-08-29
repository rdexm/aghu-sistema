package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.ExameNotificacaoVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelExamesNotificacaoId;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

public class NotificacaoResultadoExamePaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 6812205339483821776L;

	private static final String NOTIFICACAO_RESULTADO_EXAME = "notificacaoResultadoExame";

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private NotificacaoResultadoExameController notificacaoResultadoExameController;
	
	private AelExamesNotificacao exameNotificacao;
	
	private AelCampoLaudo campoLaudo;
	
	private String exaSigla;
	
	private Integer manSeq;
	
	private Integer calSeq;
	
	private VAelExameMatAnalise vAelExameMatAnalise;
	
	private DominioSituacao situacao;
	
	private List<ExameNotificacaoVO> listaExamesNotificacao;

	@Inject @Paginator
	private DynamicDataModel<ExameNotificacaoVO> dataModel;
	
	private ExameNotificacaoVO selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		this.exaSigla = null;
		this.manSeq = null;
		this.calSeq = null;
		this.vAelExameMatAnalise = null;
		this.campoLaudo = null;
		this.situacao = null;
		listaExamesNotificacao = null;
	}

	public List<ExameNotificacaoVO> getListaExamesNotificacao() {
		return listaExamesNotificacao;
	}

	public void setListaExamesNotificacao(
			List<ExameNotificacaoVO> listaExamesNotificacao) {
		this.listaExamesNotificacao = listaExamesNotificacao;
	}

	@Override
	public Long recuperarCount() {
		if(this.getvAelExameMatAnalise()!=null){
			this.exaSigla = this.getvAelExameMatAnalise().getId().getExaSigla();
			this.manSeq = this.getvAelExameMatAnalise().getId().getManSeq();
		}
		if(campoLaudo!=null){
			this.calSeq = campoLaudo.getSeq();
		}
		return examesLaudosFacade.pesquisarExamesNotificacaoCount(exaSigla, 
				manSeq, calSeq, situacao);
	}

	@Override
	public List<ExameNotificacaoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		if(this.getvAelExameMatAnalise()!=null){
			this.exaSigla = this.getvAelExameMatAnalise().getId().getExaSigla();
			this.manSeq = this.getvAelExameMatAnalise().getId().getManSeq();
		} else {
			this.exaSigla = null;
			this.manSeq = null;
		}
		if(campoLaudo!=null){
			this.calSeq = campoLaudo.getSeq();
		} else {
			this.calSeq = null;
		}
		listaExamesNotificacao = examesLaudosFacade.pesquisarExamesNotificacao(firstResult, maxResults, orderProperty, asc, 
				exaSigla, manSeq, calSeq, situacao);
		return listaExamesNotificacao;
	}
	
	public void excluir() {
		try{	
			this.cadastrosApoioExamesFacade.excluirExameNotificacao(new AelExamesNotificacaoId(selecionado.getSigla(), selecionado.getManSeq(), selecionado.getCodigo()));
			dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_EXAME_NOTIFICACAO");
			exameNotificacao = null;
		} catch(ApplicationBusinessException e){
			exameNotificacao = null;
			this.apresentarExcecaoNegocio(e);
		}
		
	}
	
	public void editarSituacao(ExameNotificacaoVO exameNotificacaoVO) {
		try {
			AelExamesNotificacaoId id = new AelExamesNotificacaoId();
			id.setCalSeq(exameNotificacaoVO.getCodigo());
			id.setEmaExaSigla(exameNotificacaoVO.getSigla());
			id.setEmaManSeq(exameNotificacaoVO.getManSeq());
			exameNotificacao = this.cadastrosApoioExamesFacade.obterExameNotificacao(id);
			
			this.cadastrosApoioExamesFacade.atualizarSituacaoExameNotificacao(exameNotificacao);
			dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SITUACAO_EXAME_NOTIFICACAO");
			exameNotificacao = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir() {
		return NOTIFICACAO_RESULTADO_EXAME;
	}
	
	public String editar() {
		notificacaoResultadoExameController.setEmaExaSigla(selecionado.getSigla());
		notificacaoResultadoExameController.setEmaManSeq(selecionado.getManSeq());
		notificacaoResultadoExameController.setCalSeq(selecionado.getCodigo());
		return NOTIFICACAO_RESULTADO_EXAME;
	}
	
	public Boolean verificarSituacao(ExameNotificacaoVO exameNotificacaoVO) {
		return (exameNotificacaoVO.getSituacao() == DominioSituacao.A);
	}

	
	public List<VAelExameMatAnalise> pesquisarMaterialAnalise(String objPesquisa) {
		return  this.returnSGWithCount(this.examesLaudosFacade.pesquisarVExamesMaterialAnalise(objPesquisa),pesquisarMaterialAnaliseCount(objPesquisa));
	}
	
	public Long pesquisarMaterialAnaliseCount(String objPesquisa) {
		return this.examesLaudosFacade.pesquisarVExamesMaterialAnaliseCount(objPesquisa);
	}
	
	public List<AelCampoLaudo> pesquisarLaudo(String objPesquisa) {
		return  this.examesFacade.pesquisarAelCampoLaudoSuggestion(objPesquisa);
	}
	
	public AelExamesNotificacao getExameNotificacao() {
		return exameNotificacao;
	}

	public void setExameNotificacao(AelExamesNotificacao exameNotificacao) {
		this.exameNotificacao = exameNotificacao;
	}

	public String getExaSigla() {
		return exaSigla;
	}

	public void setExaSigla(String exaSigla) {
		this.exaSigla = exaSigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public Integer getCalSeq() {
		return calSeq;
	}

	public void setCalSeq(Integer calSeq) {
		this.calSeq = calSeq;
	}

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public VAelExameMatAnalise getvAelExameMatAnalise() {
		return vAelExameMatAnalise;
	}

	public void setvAelExameMatAnalise(VAelExameMatAnalise vAelExameMatAnalise) {
		this.vAelExameMatAnalise = vAelExameMatAnalise;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<ExameNotificacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ExameNotificacaoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ExameNotificacaoVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ExameNotificacaoVO selecionado) {
		this.selecionado = selecionado;
	}
}
