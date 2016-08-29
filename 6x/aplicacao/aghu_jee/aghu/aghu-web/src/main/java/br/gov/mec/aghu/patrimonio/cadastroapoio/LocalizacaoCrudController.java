package br.gov.mec.aghu.patrimonio.cadastroapoio;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmEdificacao;
import br.gov.mec.aghu.model.PtmLocalizacoes;
import br.gov.mec.aghu.model.PtmLocalizacoesJn;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.PtmEdificacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class LocalizacaoCrudController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3427651592945817847L;

	@EJB 
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject	
	private  LocalizacaoListPaginatorController localizacaoListPaginatorController;
	
	private PtmLocalizacoesJn ptmLocalizacoesJn;
	
	
	private PtmLocalizacoes ptmLocalizacoes  = new PtmLocalizacoes();
	
	private PtmEdificacaoVO ptmEdificacaoVO = new PtmEdificacaoVO();
	
	private PtmEdificacao ptmEdificacao;
	
	private Long seqPtmLocalizacoes;
	
	private Integer seqPtmEdificacao;
	
	private final String PAGE_VOLTAR_LOCALIZACAO = "patrimonio-localizacaoList";
	
	private boolean isUpdate = Boolean.FALSE;	
	
	private boolean checked;
	
	private boolean situacao;
	
	private boolean editando;
	
	public enum ActionReportExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SUCESSO_EDITAR_LOCALIZACOES, MENSAGEM_SUCESSO_GRAVAR_LOCALIZACOES
	};
	
	
	
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		
	}
	
	public void iniciar(){
		if(isUpdate){
			ptmLocalizacoes = new PtmLocalizacoes();
			ptmLocalizacoes = patrimonioFacade.obterPtmLocalizacoes(seqPtmLocalizacoes);
			if(ptmLocalizacoes.getIndSituacao() != null){
				situacao=ptmLocalizacoes.getIndSituacao().isAtivo();
			}
			ptmLocalizacoes.setEdificacao(patrimonioFacade.obterPtmEdificacaoPorSeq(seqPtmEdificacao));
			ptmEdificacaoVO = new PtmEdificacaoVO();
			ptmEdificacaoVO.setDescricao(ptmLocalizacoes.getEdificacao().getDescricao());
			ptmEdificacaoVO.setNome(ptmLocalizacoes.getEdificacao().getNome());
			ptmEdificacaoVO.setNumeroBem(ptmLocalizacoes.getEdificacao().getPtmBemPermanentes().getNumeroBem());
			ptmEdificacaoVO.setSeq(ptmLocalizacoes.getEdificacao().getSeq());
			editando = Boolean.TRUE;
			
		}else{
			ptmLocalizacoes = new PtmLocalizacoes();
			ptmEdificacaoVO = null;
			situacao= Boolean.TRUE;
			checked = Boolean.TRUE;
			editando = Boolean.FALSE;
		}
	}
	
	
	public String gravar() throws ApplicationBusinessException{
		try {
			String mensagemSucesso = null;
			if(ptmLocalizacoes.getSeq() != null){
				if(situacao){
					ptmLocalizacoes.setIndSituacao(DominioSituacao.A);
				}else{
					ptmLocalizacoes.setIndSituacao(DominioSituacao.I);
				}
				ptmLocalizacoes.setDataAlteradoEm(new Date());
				ptmLocalizacoes.setServidorAlteradoPor(getServidorLogadoFacade().obterServidorLogado());
				ptmLocalizacoes.setEdificacao(patrimonioFacade.obterPtmEdificacaoPorSeq(ptmEdificacaoVO.getSeq()));
				patrimonioFacade.atualizarPtmLocalizacoes(ptmLocalizacoes);
				
				
				ptmLocalizacoesJn = new PtmLocalizacoesJn();
				ptmLocalizacoesJn.setSeq(ptmLocalizacoes.getSeq());
				ptmLocalizacoesJn.setCentroCusto(ptmLocalizacoes.getCentroCusto());
				ptmLocalizacoesJn.setDataAlteradoEm(ptmLocalizacoes.getDataAlteradoEm());
				ptmLocalizacoesJn.setDataCriacao(ptmLocalizacoes.getDataCriacao());
				ptmLocalizacoesJn.setDescricao(ptmLocalizacoes.getDescricao());
				ptmLocalizacoesJn.setEdificacao(ptmLocalizacoes.getEdificacao());
				ptmLocalizacoesJn.setIndSituacao(ptmLocalizacoes.getIndSituacao());
				ptmLocalizacoesJn.setNome(ptmLocalizacoes.getNome());
				ptmLocalizacoesJn.setNomeUsuario(this.obterLoginUsuarioLogado());
				ptmLocalizacoesJn.setOperacao(DominioOperacoesJournal.UPD);
				ptmLocalizacoesJn.setServidor(ptmLocalizacoes.getServidor());
				ptmLocalizacoesJn.setServidorAlteradoPor(ptmLocalizacoes.getServidorAlteradoPor());
				patrimonioFacade.inserirPtmLocalizacoesJN(ptmLocalizacoesJn);
				
				ptmLocalizacoes = null;
				mensagemSucesso = "MENSAGEM_SUCESSO_EDITAR_LOCALIZACOES";
				localizacaoListPaginatorController.pesquisar();
				apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
				return PAGE_VOLTAR_LOCALIZACAO;
			}else{
				if(situacao){
					ptmLocalizacoes.setIndSituacao(DominioSituacao.A);
				}else{
					ptmLocalizacoes.setIndSituacao(DominioSituacao.I);
				}
				ptmEdificacao = patrimonioFacade.obterPtmEdificacaoPorSeq(ptmEdificacaoVO.getSeq());
				ptmLocalizacoes.setEdificacao(ptmEdificacao);
				ptmLocalizacoes.setDataCriacao(new Date());
				ptmLocalizacoes.setServidor(getServidorLogadoFacade().obterServidorLogado());
				patrimonioFacade.persistirPtmLocalizacoes(ptmLocalizacoes);
				
				
				ptmLocalizacoesJn = new PtmLocalizacoesJn();
				ptmLocalizacoesJn.setCentroCusto(ptmLocalizacoes.getCentroCusto());
				ptmLocalizacoesJn.setDataAlteradoEm(ptmLocalizacoes.getDataAlteradoEm());
				ptmLocalizacoesJn.setDataCriacao(ptmLocalizacoes.getDataCriacao());
				ptmLocalizacoesJn.setDescricao(ptmLocalizacoes.getDescricao());
				ptmLocalizacoesJn.setEdificacao(ptmLocalizacoes.getEdificacao());
				ptmLocalizacoesJn.setIndSituacao(ptmLocalizacoes.getIndSituacao());
				ptmLocalizacoesJn.setNome(ptmLocalizacoes.getNome());
				ptmLocalizacoesJn.setNomeUsuario(this.obterLoginUsuarioLogado());
				ptmLocalizacoesJn.setOperacao(DominioOperacoesJournal.INS);
				ptmLocalizacoesJn.setSeq(ptmLocalizacoes.getSeq());
				ptmLocalizacoesJn.setServidor(ptmLocalizacoes.getServidor());
				ptmLocalizacoesJn.setServidorAlteradoPor(ptmLocalizacoes.getServidorAlteradoPor());
				patrimonioFacade.inserirPtmLocalizacoesJN(ptmLocalizacoesJn);
				
				ptmLocalizacoes = null;
				mensagemSucesso = "MENSAGEM_SUCESSO_GRAVAR_LOCALIZACOES";
				localizacaoListPaginatorController.pesquisar();
				apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
				return PAGE_VOLTAR_LOCALIZACAO;
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return "exception/erro";
		}
	}
	
	public String voltar(){
		return PAGE_VOLTAR_LOCALIZACAO;
	}
	
    /**
	 * 
	 * Obtem o centro de custo do suggestion
	 */
	public List<FccCentroCustos> obterCentroCusto(final String objPesquisa){
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return returnSGWithCount(
					this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemOuDescricao(objPesquisa),			
					this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemOuDescricaoCount(objPesquisa));
		}else {
			return returnSGWithCount(
					this.centroCustoFacade.pesquisarCentroCustosAtivos(objPesquisa),
					this.centroCustoFacade.obterCentroCustoAtivosCount());
		}
	}
	
     /**
	 * 
	 * Obtem edificação do suggestion
	 */
	public List<PtmEdificacaoVO> obteSbEdificacao(final String objPesquisa){
			return returnSGWithCount(
					this.patrimonioFacade.pesquisarEdificacoesAtivasNrBemouSeqouNome(objPesquisa),			
					this.patrimonioFacade.pesquisarEdificacoesAtivasNrBemouSeqouNomeCount(objPesquisa));
	}


	public PtmLocalizacoes getPtmLocalizacoes() {
		return ptmLocalizacoes;
	}

	public void setPtmLocalizacoes(PtmLocalizacoes ptmLocalizacoes) {
		this.ptmLocalizacoes = ptmLocalizacoes;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public boolean isSituacao() {
		return situacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public Long getSeqPtmLocalizacoes() {
		return seqPtmLocalizacoes;
	}

	public void setSeqPtmLocalizacoes(Long seqPtmLocalizacoes) {
		this.seqPtmLocalizacoes = seqPtmLocalizacoes;
	}

	public PtmEdificacaoVO getPtmEdificacaoVO() {
		return ptmEdificacaoVO;
	}

	public void setPtmEdificacaoVO(PtmEdificacaoVO ptmEdificacaoVO) {
		this.ptmEdificacaoVO = ptmEdificacaoVO;
	}

	public PtmEdificacao getPtmEdificacao() {
		return ptmEdificacao;
	}

	public void setPtmEdificacao(PtmEdificacao ptmEdificacao) {
		this.ptmEdificacao = ptmEdificacao;
	}

	public Integer getSeqPtmEdificacao() {
		return seqPtmEdificacao;
	}

	public void setSeqPtmEdificacao(Integer seqPtmEdificacao) {
		this.seqPtmEdificacao = seqPtmEdificacao;
	}


	

}
