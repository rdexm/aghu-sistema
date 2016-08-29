package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoIntercorrenciaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaJnVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCadastroTiposIntercorrentesPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1051401596018431340L;

	private static final String PAGE_CADASTRAR_TIPOS_INTERCORRENTES = "procedimentoterapeutico-manterCadastroTiposIntercorrentesCRUD";
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private List<MptTipoIntercorrenciaJnVO> listaHistoricoTipoIntercorrenciaJn;

	private MptTipoIntercorrencia tipoIntercorrenteSelecionado;
	
	private MptTipoIntercorrenciaJn historicoAlteracaoSelecionado;
		
	private String descricao;
	
	private String descricaoModal;
	
	private DominioSituacao situacao;
	
	private boolean situacaoModificada;
	
	private boolean situacaoInativaAlterada = false;
	
	@Inject
	@Paginator
	private DynamicDataModel<MptTipoIntercorrencia> dataModel;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacede;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		
		if (this.situacaoInativaAlterada) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TIPO_INTERCORRENTE_EDITADA_COM_SUCESSO");
			this.situacaoInativaAlterada = false;
		}
	}
	
	public void pesquisar(){
		dataModel.setPesquisaAtiva(true);
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		this.descricao = null;
		this.situacao = null;
		this.tipoIntercorrenteSelecionado = null;
		this.historicoAlteracaoSelecionado = null;
		dataModel.setPesquisaAtiva(false);
		dataModel.limparPesquisa();
	}
	
	public String inserirEditar(){
		return PAGE_CADASTRAR_TIPOS_INTERCORRENTES;
	}
	
	public String truncarString(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}
	
	public List<MptTipoIntercorrenciaJnVO> historicoIntercorrente(MptTipoIntercorrencia item) {
		setTipoIntercorrenteSelecionado(item);
		List<MptTipoIntercorrenciaJnVO> listaHistoricoAlteracaoVO = new ArrayList<MptTipoIntercorrenciaJnVO>();
		listaHistoricoAlteracaoVO = this.procedimentoTerapeuticoFacede.obterHistoricoTiposIntercorrentes(item.getSeq().intValue());
	
		if(!listaHistoricoAlteracaoVO.isEmpty()){
			if(CoreUtil.modificados(item.getIndSituacao(), listaHistoricoAlteracaoVO.get(0).getIndSituacao())){
				listaHistoricoAlteracaoVO.get(0).setColor("#FFFF7E");
			}
		}
		return listaHistoricoAlteracaoVO;
	}
	
	public String obterUsuario(Integer serMatricula, Integer vinCodigo){
		RapServidores servidor = servidorLogadoFacade.obterServidorPorChavePrimaria(serMatricula, vinCodigo.shortValue());
		return servidor.getUsuario();
	}
	
	@Override
	public Long recuperarCount() {
		return procedimentoTerapeuticoFacede.listarTiposIntercorrentesCount(this.descricao, this.situacao);
	}

	@Override
	public List<MptTipoIntercorrencia> recuperarListaPaginada(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc) {
		return procedimentoTerapeuticoFacede.listarTiposIntercorrentes(firstResult, maxResult, orderProperty, asc, this.descricao, this.situacao);
	}
	
	//Getters and Setters
	public List<MptTipoIntercorrenciaJnVO> getListaHistoricoTipoIntercorrenciaJn() {
		return listaHistoricoTipoIntercorrenciaJn;
	}

	public void setListaHistoricoTipoIntercorrenciaJn(
			List<MptTipoIntercorrenciaJnVO> listaHistoricoTipoIntercorrenciaJn) {
		this.listaHistoricoTipoIntercorrenciaJn = listaHistoricoTipoIntercorrenciaJn;
	}

	public MptTipoIntercorrencia getTipoIntercorrenteSelecionado() {
		return tipoIntercorrenteSelecionado;
	}

	public void setTipoIntercorrenteSelecionado(
			MptTipoIntercorrencia tipoIntercorrenteSelecionado) {
		this.tipoIntercorrenteSelecionado = tipoIntercorrenteSelecionado;
	}
	
	public MptTipoIntercorrenciaJn getHistoricoAlteracaoSelecionado() {
		return historicoAlteracaoSelecionado;
	}

	public void setHistoricoAlteracaoSelecionado(
			MptTipoIntercorrenciaJn historicoAlteracaoSelecionado) {
		this.historicoAlteracaoSelecionado = historicoAlteracaoSelecionado;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoModal() {
		return descricaoModal;
	}

	public void setDescricaoModal(String descricaoModal) {
		this.descricaoModal = descricaoModal;
	}
	
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<MptTipoIntercorrencia> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MptTipoIntercorrencia> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isSituacaoModificada() {
		return situacaoModificada;
	}

	public void setSituacaoModificada(boolean situacaoModificada) {
		this.situacaoModificada = situacaoModificada;
	}
	
	public boolean isSituacaoInativaAlterada() {
		return situacaoInativaAlterada;
	}

	public void setSituacaoInativaAlterada(boolean situacaoInativaAlterada) {
		this.situacaoInativaAlterada = situacaoInativaAlterada;
	}
}
