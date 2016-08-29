package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExameResuNotificacao;
import br.gov.mec.aghu.model.AelExameResuNotificacaoId;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelExamesNotificacaoId;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class NotificacaoResultadoExameController extends ActionController {


	private static final long serialVersionUID = 6336956796374531081L;


	private static final String PESQUISA_NOTIFICACAO_RESULTADO_EXAME = "pesquisaNotificacaoResultadoExame";
	
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private String emaExaSigla;
	private Integer emaManSeq;
	private Integer calSeq;
	private Short seqp;
	
	private AelExamesNotificacao exameNotificacao;
	private VAelExameMatAnalise vAelExameMatAnalise;
	private AelCampoLaudo campoLaudo;
	private Boolean situacao;
	private AelResultadoCodificado resultadoCodificado;
	private Long resultadoNumerico;
	private String resultadoAlfanumerico;
	private Boolean situacaoResultadoNotificado = true;
	private AelExameResuNotificacao exameResultadoNotificacao;
	private List<AelExameResuNotificacao> listaExameResultadoNotificacao;
	
	private Boolean edicao;
	private Boolean edicaoNotificacaoExame;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (this.emaExaSigla != null && emaManSeq != null && calSeq != null) {
			exameNotificacao = this.examesLaudosFacade.retornaExameNotificacaoPorId(new AelExamesNotificacaoId(emaExaSigla, emaManSeq, calSeq));
			
			if(exameNotificacao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			AelExamesMaterialAnaliseId vAelExameMatAnaliseId = new AelExamesMaterialAnaliseId();
			vAelExameMatAnaliseId.setExaSigla(getEmaExaSigla());
			vAelExameMatAnaliseId.setManSeq(getEmaManSeq());
			this.vAelExameMatAnalise = this.examesFacade.obterVAelExameMaterialAnalise(vAelExameMatAnaliseId);
			this.campoLaudo = exameNotificacao.getCampoLaudo();
			
			if(exameNotificacao.getSituacao().equals(DominioSituacao.A)){
				this.situacao = true;	
			} else {
				this.situacao = false;
			}
			
			listaExameResultadoNotificacao = this.cadastrosApoioExamesFacade.pesquisarExameResultadoNotificacao(getEmaExaSigla(),getEmaManSeq() , getCalSeq());
			this.edicaoNotificacaoExame = true;
		} else {
			vAelExameMatAnalise = null;
			this.campoLaudo = null;
			exameNotificacao = null;
			listaExameResultadoNotificacao = null;
			this.situacao = true;
			this.edicaoNotificacaoExame = false;
		}
		
		return null;
	
	}
	
	public List<VAelExameMatAnalise> pesquisarMaterialAnalise(String objPesquisa) {
		return this.returnSGWithCount(this.examesLaudosFacade.pesquisarVExamesMaterialAnalise(objPesquisa),pesquisarMaterialAnaliseCount(objPesquisa));
	}
	
	public Long pesquisarMaterialAnaliseCount(String objPesquisa) {
		return this.examesLaudosFacade.pesquisarVExamesMaterialAnaliseCount(objPesquisa);
	}
	
	public List<AelCampoLaudo> pesquisarCampoLaudo(String objPesquisa) {
		if(this.getvAelExameMatAnalise()!=null){
			return this.cadastrosApoioExamesFacade.pesquisarCampoLaudoExameMaterial(objPesquisa, this.getvAelExameMatAnalise().getId().getExaSigla(), this.getvAelExameMatAnalise().getId().getManSeq());
		} else {
			return null;	
		}
	}
	
	public List<AelResultadoCodificado> pesquisarResultadoCodificado(String objPesquisa) {
		try {
			return cadastrosApoioExamesFacade.sbListarResultadoCodificado((String) objPesquisa, getCalSeq());
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void confirmar() {
		try {
			DominioSituacao situacaoExame;
			if(this.getSituacao()){
				situacaoExame = DominioSituacao.A;
			} else {
				situacaoExame = DominioSituacao.I;
			}
			
			this.emaExaSigla = vAelExameMatAnalise.getId().getExaSigla();
			this.emaManSeq = vAelExameMatAnalise.getId().getManSeq();
			this.calSeq = this.getCampoLaudo().getSeq();
			this.cadastrosApoioExamesFacade.persistirExameNotificacao(new AelExamesNotificacaoId(emaExaSigla, emaManSeq, calSeq), situacaoExame);
			this.apresentarMsgNegocio(Severity.INFO,"MSG_NOTIFICACAO_EXAME_SUCESSO");	
			this.edicaoNotificacaoExame = true;
		} catch (BaseException  e) {
			this.apresentarExcecaoNegocio(e);
		}
		this.exameResultadoNotificacao = null;
	}
	
	public void gravar(){
		try{
			AelExameResuNotificacao exameResultadoNotificacao = new AelExameResuNotificacao();
			AelExameResuNotificacaoId id = new AelExameResuNotificacaoId();
			id.setExnCalSeq(this.getCampoLaudo().getSeq());
			id.setExnEmaExaSigla(vAelExameMatAnalise.getId().getExaSigla());
			id.setExnEmaManSeq(vAelExameMatAnalise.getId().getManSeq());
			id.setSeqp(this.cadastrosApoioExamesFacade.obterExameResultadoNotificacaoNextSeqp(id));
			exameResultadoNotificacao.setId(id);
			exameResultadoNotificacao.setAelExamesNotificacao(exameNotificacao);
			exameResultadoNotificacao.setAelResultadoCodificado(resultadoCodificado);
			exameResultadoNotificacao.setCriadoEm(new Date());
			
			if(this.getSituacaoResultadoNotificado()){
				exameResultadoNotificacao.setSituacao(DominioSituacao.A);	
			} else {
				exameResultadoNotificacao.setSituacao(DominioSituacao.I);
			}

			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			exameResultadoNotificacao.setRapServidores(servidorLogado);
			
			exameResultadoNotificacao.setResultadoAlfanum(resultadoAlfanumerico);
			exameResultadoNotificacao.setResultadoNumExp(resultadoNumerico);
			this.cadastrosApoioExamesFacade.persistirExameResultadoNotificacao(exameResultadoNotificacao);
			this.apresentarMsgNegocio(Severity.INFO,"MSG_NOTIFICACAO_EXAME_RESULTADO_INCLUIDO_SUCESSO");
			edicao = false;	
			this.resultadoCodificado = null;
			this.resultadoAlfanumerico = null;
			this.resultadoNumerico = null;
			this.situacaoResultadoNotificado = true;
			listaExameResultadoNotificacao = this.cadastrosApoioExamesFacade.pesquisarExameResultadoNotificacao(this.getEmaExaSigla(), this.getEmaManSeq(), this.getCalSeq());
			
		} catch(BaseException  e){
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void atualizar() {
		try{
			exameResultadoNotificacao.setAelExamesNotificacao(exameNotificacao);
			exameResultadoNotificacao.setAelResultadoCodificado(resultadoCodificado);
			exameResultadoNotificacao.setCriadoEm(new Date());
			if(this.getSituacaoResultadoNotificado()){
				exameResultadoNotificacao.setSituacao(DominioSituacao.A);	
			} else {
				exameResultadoNotificacao.setSituacao(DominioSituacao.I);
			}
			exameResultadoNotificacao.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			exameResultadoNotificacao.setResultadoAlfanum(resultadoAlfanumerico);
			exameResultadoNotificacao.setResultadoNumExp(resultadoNumerico);
			this.cadastrosApoioExamesFacade.atualizarExameResultadoNotificacao(exameResultadoNotificacao);
			this.apresentarMsgNegocio(Severity.INFO,"MSG_NOTIFICACAO_EXAME_RESULTADO_ALTERADO_SUCESSO");
			edicao = false;	
			this.resultadoCodificado = null;
			this.resultadoAlfanumerico = null;
			this.resultadoNumerico = null;
			this.situacaoResultadoNotificado = true;
			this.exameResultadoNotificacao = null;
			listaExameResultadoNotificacao = this.cadastrosApoioExamesFacade.pesquisarExameResultadoNotificacao(this.getEmaExaSigla(), this.getEmaManSeq(), this.getCalSeq());
		} catch(ApplicationBusinessException e){
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir() {
		try {
			this.cadastrosApoioExamesFacade.excluirExameResultadoNotificacao(exameResultadoNotificacao.getId());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_NOTIFICACAO_EXAME_RESULTADO");
			exameNotificacao = null;
			edicao = false;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
		listaExameResultadoNotificacao = this.cadastrosApoioExamesFacade.pesquisarExameResultadoNotificacao(this.getEmaExaSigla(),this.getEmaManSeq() , this.getCalSeq());
	}
	
	public void limparSuggestions(){
		this.campoLaudo = null;
	}
	
	public void editar(AelExameResuNotificacao exameResultadoNotificacao) {
		this.exameResultadoNotificacao = exameResultadoNotificacao;
		this.resultadoAlfanumerico = exameResultadoNotificacao.getResultadoAlfanum();
		this.resultadoCodificado = exameResultadoNotificacao.getAelResultadoCodificado();
		this.resultadoNumerico = exameResultadoNotificacao.getResultadoNumExp();
		edicao = true;
	}
	
	public void cancelarEdicao() {
		this.exameResultadoNotificacao = null;
		this.resultadoAlfanumerico = null;
		this.resultadoCodificado = null;
		this.resultadoNumerico = null;
		this.seqp = null;
		this.situacaoResultadoNotificado = true;
		edicao = false;
	}
	
	public String cancelar() {
		this.exameResultadoNotificacao = null;
		this.resultadoAlfanumerico = null;
		this.resultadoCodificado = null;
		this.resultadoNumerico = null;
		this.seqp = null;
		this.situacaoResultadoNotificado = true;
		edicao = false;
		this.listaExameResultadoNotificacao = null;
		this.vAelExameMatAnalise = null;
		this.campoLaudo=null;
		this.situacao = true;
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.calSeq = null;
		return PESQUISA_NOTIFICACAO_RESULTADO_EXAME;
	}

	public Boolean getSituacaoResultadoNotificado() {
		return situacaoResultadoNotificado;
	}

	public void setSituacaoResultadoNotificado(
			Boolean situacaoResultadoNotificado) {
		this.situacaoResultadoNotificado = situacaoResultadoNotificado;
	}

	public Long getResultadoNumerico() {
		return resultadoNumerico;
	}

	public void setResultadoNumerico(Long resultadoNumerico) {
		this.resultadoNumerico = resultadoNumerico;
	}

	public String getResultadoAlfanumerico() {
		return resultadoAlfanumerico;
	}

	public void setResultadoAlfanumerico(String resultadoAlfanumerico) {
		this.resultadoAlfanumerico = resultadoAlfanumerico;
	}

	public void selecionarExameResultadoNotificacao(
			AelExameResuNotificacao exameResultadoNotificacao) {
		this.exameResultadoNotificacao = exameResultadoNotificacao;
	}

	public AelExamesNotificacao getExameNotificacao() {
		return exameNotificacao;
	}

	public void setExameNotificacao(AelExamesNotificacao exameNotificacao) {
		this.exameNotificacao = exameNotificacao;
	}

	public VAelExameMatAnalise getvAelExameMatAnalise() {
		return vAelExameMatAnalise;
	}

	public void setvAelExameMatAnalise(VAelExameMatAnalise vAelExameMatAnalise) {
		this.vAelExameMatAnalise = vAelExameMatAnalise;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public AelResultadoCodificado getResultadoCodificado() {
		return resultadoCodificado;
	}

	public void setResultadoCodificado(
			AelResultadoCodificado resultadoCodificado) {
		this.resultadoCodificado = resultadoCodificado;
	}

	public AelExameResuNotificacao getExameResultadoNotificacao() {
		return exameResultadoNotificacao;
	}

	public void setExameResultadoNotificacao(
			AelExameResuNotificacao exameResultadoNotificacao) {
		this.exameResultadoNotificacao = exameResultadoNotificacao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}

	public void setCadastrosApoioExamesFacade(
			ICadastrosApoioExamesFacade cadastrosApoioExamesFacade) {
		this.cadastrosApoioExamesFacade = cadastrosApoioExamesFacade;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public List<AelExameResuNotificacao> getListaExameResultadoNotificacao() {
		return listaExameResultadoNotificacao;
	}

	public void setListaExameResultadoNotificacao(
			List<AelExameResuNotificacao> listaExameResultadoNotificacao) {
		this.listaExameResultadoNotificacao = listaExameResultadoNotificacao;
	}

	public Boolean getEdicaoNotificacaoExame() {
		return edicaoNotificacaoExame;
	}

	public void setEdicaoNotificacaoExame(Boolean edicaoNotificacaoExame) {
		this.edicaoNotificacaoExame = edicaoNotificacaoExame;
	}

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Integer getCalSeq() {
		return calSeq;
	}

	public void setCalSeq(Integer calSeq) {
		this.calSeq = calSeq;
	}
}