package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ComorbidadeController extends ActionController {

	
	private static final long serialVersionUID = -8194798745495396555L;
	private static final String PAGE_COMORBIDADE_LIST = "comorbidadeList";
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private MtxComorbidade mtxComorbidade = new MtxComorbidade();
	private List<String> listaDomTipoTransplante = new ArrayList<String>();
	private List<String> listaDomTipoTransplanteSelecionado = new ArrayList<String>();
	private Boolean indAtivo = Boolean.TRUE;
	private Boolean modoEdicao = Boolean.FALSE;
	private Boolean modoVisualizacao = Boolean.FALSE;
	private String diagnostico;
	private Integer escore;
	private AghCid cid;
	private String mensagem;
	private boolean gravando;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		carregarListaTipoTransplante();
		if(modoEdicao || modoVisualizacao){
			carregarEdicaoVisualizacao();
		}else{
			indAtivo = true;
		}
	}
	
	/**Realiza a inserção ou update de um registro na tabela**/
	public String gravar(){
		gravando = true;
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		try {
			if(modoEdicao == false){//gravar
				mtxComorbidade = new MtxComorbidade();
				mtxComorbidade.setCriadoEm(new Date());
				mensagem = "MENSAGEM_SUCESSO_INCLUSAO_COMORBIDADE";
			}else{
				mensagem = "MENSAGEM_SUCESSO_EDICAO_COMORBIDADE";
			}
			mtxComorbidade.setDescricao(diagnostico != null ? diagnostico.toUpperCase().trim() : diagnostico);
			mtxComorbidade.setEscore(escore);
			mtxComorbidade.setCidSeq(cid);
			mtxComorbidade.setServidor(servidor);
			mtxComorbidade.setSituacao(DominioSituacao.getInstance(indAtivo));
			mtxComorbidade.setTipo(DominioTipoTransplante.getInstance(listaDomTipoTransplanteSelecionado));
			transplanteFacade.validarInclusaoComorbidade(mtxComorbidade);
			transplanteFacade.gravarAtualizarComorbidade(mtxComorbidade);
			this.apresentarMsgNegocio(Severity.INFO, mensagem);
			return voltarPagPesquisa();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public List<AghCid> pesquisarCid(String pesquisa) {
		gravando = false;
		return this.returnSGWithCount(this.transplanteFacade.pesquisarCidPorSeqCodDescricao(pesquisa),this.transplanteFacade.pesquisarCidPorSeqCodDescricaoCount(pesquisa));
	}
	
	private void carregarListaTipoTransplante(){
		if(listaDomTipoTransplante.isEmpty()){
			listaDomTipoTransplante.add(DominioTipoTransplante.M.getDescricao());
			listaDomTipoTransplante.add(DominioTipoTransplante.O.getDescricao());
		}
	}
	
	public void limparDiagnosticoDiverso(){
		if(cid != null && !gravando){
			diagnostico = null;
		}
	}
	
	public void limparInclusao(){
		indAtivo = null;
		diagnostico = null;
		escore = null;
		modoEdicao = false;
		modoVisualizacao = false;
		mtxComorbidade = new MtxComorbidade();
		listaDomTipoTransplanteSelecionado = new ArrayList<String>();
		cid = null;
	}
	
	public String voltarPagPesquisa() {
		limparInclusao();
		return PAGE_COMORBIDADE_LIST;
	}
	
	public void carregarEdicaoVisualizacao(){
		indAtivo = mtxComorbidade.getSituacao().isAtivo();
		escore = mtxComorbidade.getEscore();
		cid = mtxComorbidade.getCidSeq();
		if(cid != null){
			mtxComorbidade.setDescricao(null);
		}
		diagnostico = mtxComorbidade.getDescricao();
		if(mtxComorbidade.getTipo().toString().equals("X")){
			listaDomTipoTransplanteSelecionado.add(DominioTipoTransplante.M.getDescricao());
			listaDomTipoTransplanteSelecionado.add(DominioTipoTransplante.O.getDescricao());
		}else{
			listaDomTipoTransplanteSelecionado.add( mtxComorbidade.getTipo().getDescricao());
		}
	}
	
	//Gets e Sets
	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public MtxComorbidade getMtxComorbidade() {
		return mtxComorbidade;
	}

	public void setMtxComorbidade(MtxComorbidade mtxComorbidade) {
		this.mtxComorbidade = mtxComorbidade;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}


	public List<String> getListaDomTipoTransplante() {
		return listaDomTipoTransplante;
	}

	public void setListaDomTipoTransplante(List<String> listaDomTipoTransplante) {
		this.listaDomTipoTransplante = listaDomTipoTransplante;
	}

	public List<String> getListaDomTipoTransplanteSelecionado() {
		return listaDomTipoTransplanteSelecionado;
	}

	public void setListaDomTipoTransplanteSelecionado(List<String> listaDomTipoTransplanteSelecionado) {
		this.listaDomTipoTransplanteSelecionado = listaDomTipoTransplanteSelecionado;
	}

	public Boolean getModoVisualizacao() {
		return modoVisualizacao;
	}

	public void setModoVisualizacao(Boolean modoVisualizacao) {
		this.modoVisualizacao = modoVisualizacao;
	}

	public Integer getEscore() {
		return escore;
	}

	public void setEscore(Integer escore) {
		this.escore = escore;
	}
	
	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

}
