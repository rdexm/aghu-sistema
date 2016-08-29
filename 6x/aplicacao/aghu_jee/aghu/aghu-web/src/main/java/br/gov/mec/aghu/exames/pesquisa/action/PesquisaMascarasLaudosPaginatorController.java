package br.gov.mec.aghu.exames.pesquisa.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class PesquisaMascarasLaudosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2164708104293467978L;

	private static final String MANTER_MASCARAS_LAUDOS_CRUD = "exames-manterMascarasLaudosCRUD";

	private static final String MASCARA_EXAMES_EDITOR = "exames-mascaraExamesEditor";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private DominioSituacaoVersaoLaudo situacao;
	private VAelExameMatAnalise exameMatAnalise;
	private String nomeDesenho; // Máscara

	private AelVersaoLaudo versaoLaudo;

	//campos de parametros para a tela desenho da mascara
	private String emaExaSigla;
	private Integer emaManSeq;
	private Integer seqp;
	
	private boolean voltouDesenhoNovaVersaoMascara;
	private String novaVersaoExaSigla;
	private Integer novaVersaoManSeq;
	private Short novaVersaoUnfSeq;
	private DominioSituacaoVersaoLaudo novaVersaoSituacao;
	private String novaVersaoNomeDesenho;
	private Integer novaVersaoPesquisaPaginada;

	@Inject @Paginator
	private DynamicDataModel<AelVersaoLaudo> dataModel;
	
	private AelVersaoLaudo selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void iniciar() {
	 

		
		try {
			
			if(this.voltouDesenhoNovaVersaoMascara){
				try{
					this.situacao = this.novaVersaoSituacao;
					this.nomeDesenho = this.novaVersaoNomeDesenho;
					if(this.novaVersaoExaSigla != null && this.novaVersaoManSeq != null){
						this.exameMatAnalise = this.cadastrosApoioExamesFacade.buscarVAelExameMatAnalisePelaSiglaESeq(this.novaVersaoExaSigla, this.novaVersaoManSeq);
					}
				}finally{
					dataModel.reiniciarPaginator();
					this.novaVersaoExaSigla = null;
					this.novaVersaoManSeq = null;
					this.novaVersaoUnfSeq = null;
					this.novaVersaoSituacao = null;
					this.novaVersaoNomeDesenho = null;
					this.voltouDesenhoNovaVersaoMascara = false;
				}
			}
			
			// Obtem o usuario da unidade executora
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		
			if(usuarioUnidadeExecutora!=null){
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}

		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}
	
	}


	/**
	 * Recupera uma instancia com os filtros de pesquisa atualizados
	 */
	private AelVersaoLaudo getElementoFiltroPesquisa() {

		final AelVersaoLaudo elementoFiltroPesquisa = new AelVersaoLaudo();

		// Popula filtro de pesquisa
		elementoFiltroPesquisa.setSituacao(this.situacao);
		
		// Filtro exame material de Analise
		if(this.exameMatAnalise != null){
			AelExamesMaterialAnalise exameMaterialAnalise = this.examesFacade.obterAelExamesMaterialAnalisePorId(this.exameMatAnalise.getId());
			elementoFiltroPesquisa.setExameMaterialAnalise(exameMaterialAnalise);
		}
		
		if (StringUtils.isNotEmpty(this.nomeDesenho)) {
			elementoFiltroPesquisa.setNomeDesenho(this.nomeDesenho.trim());
		}
		return elementoFiltroPesquisa;
	}

	/**
	 * Pesquisa principal
	 */
	public void pesquisar() {
		// Reseta a unidade executora associada ao usuário
		if(usuarioUnidadeExecutora!=null){
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.pesquisarVersaoLaudoCount(this.unidadeExecutora, this.getElementoFiltroPesquisa());
	}

	@Override
	public List<AelVersaoLaudo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.cadastrosApoioExamesFacade.pesquisarVersaoLaudo(firstResult, maxResult, orderProperty, asc, this.unidadeExecutora, this.getElementoFiltroPesquisa());
	}

	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.situacao = null;
		this.exameMatAnalise = null;
		this.nomeDesenho = null;
		
		// Obtem o usuario da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora=null;
		}

		if (this.usuarioUnidadeExecutora != null) {
			// Reseta a unidade executora associada ao usuário
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}
		
		
	}
	
	public String criarNovaVersao(AelVersaoLaudo versaoLaudo) throws ParserConfigurationException, SAXException, IOException{
		try {
			cadastrosApoioExamesFacade.verificarSituacaoDuplicada(versaoLaudo);
			return MASCARA_EXAMES_EDITOR;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Obtém a unidade executora de exames
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(final String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}
	
	/**
	 * Persiste a unidade executora do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			this.usuarioUnidadeExecutora = this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Pesquisa exame material de análise
	 */
	public List<VAelExameMatAnalise> obterExameMaterialAnalise(String objPesquisa) {
		return this.returnSGWithCount(cadastrosApoioExamesFacade.buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise((String) objPesquisa),obterExameMaterialAnaliseCount(objPesquisa));
	}
	
	public Long obterExameMaterialAnaliseCount(String objPesquisa) {
		return cadastrosApoioExamesFacade.buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnaliseCount((String) objPesquisa);
	}
	
	public void excluir()  {
		try {
			cadastrosApoioExamesFacade.removerVersaoLaudo(selecionado.getId());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_MASCARA_LAUDOS");

		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_MASCARAS_LAUDOS_CRUD;
	}
	
	public String editar(){
		return MANTER_MASCARAS_LAUDOS_CRUD;
	}
	
	public String abrirEditorMascaraExames(){
		return MASCARA_EXAMES_EDITOR;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public DominioSituacaoVersaoLaudo getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoVersaoLaudo situacao) {
		this.situacao = situacao;
	}

	public VAelExameMatAnalise getExameMatAnalise() {
		return exameMatAnalise;
	}

	public void setExameMatAnalise(VAelExameMatAnalise exameMatAnalise) {
		this.exameMatAnalise = exameMatAnalise;
	}

	public String getNomeDesenho() {
		return nomeDesenho;
	}

	public void setNomeDesenho(String nomeDesenho) {
		this.nomeDesenho = nomeDesenho;
	}

	public AelVersaoLaudo getVersaoLaudo() {
		return versaoLaudo;
	}

	public void setVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		this.versaoLaudo = versaoLaudo;
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

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public Short getNovaVersaoUnfSeq() {
		return novaVersaoUnfSeq;
	}

	public void setNovaVersaoUnfSeq(Short novaVersaoUnfSeq) {
		this.novaVersaoUnfSeq = novaVersaoUnfSeq;
	}

	public DominioSituacaoVersaoLaudo getNovaVersaoSituacao() {
		return novaVersaoSituacao;
	}

	public void setNovaVersaoSituacao(
			DominioSituacaoVersaoLaudo novaVersaoSituacao) {
		this.novaVersaoSituacao = novaVersaoSituacao;
	}

	public String getNovaVersaoNomeDesenho() {
		return novaVersaoNomeDesenho;
	}

	public void setNovaVersaoNomeDesenho(String novaVersaoNomeDesenho) {
		this.novaVersaoNomeDesenho = novaVersaoNomeDesenho;
	}

	public Integer getNovaVersaoPesquisaPaginada() {
		return novaVersaoPesquisaPaginada;
	}

	public void setNovaVersaoPesquisaPaginada(Integer novaVersaoPesquisaPaginada) {
		this.novaVersaoPesquisaPaginada = novaVersaoPesquisaPaginada;
	}

	public boolean isVoltouDesenhoNovaVersaoMascara() {
		return voltouDesenhoNovaVersaoMascara;
	}

	public void setVoltouDesenhoNovaVersaoMascara(
			boolean voltouDesenhoNovaVersaoMascara) {
		this.voltouDesenhoNovaVersaoMascara = voltouDesenhoNovaVersaoMascara;
	}

	public String getNovaVersaoExaSigla() {
		return novaVersaoExaSigla;
	}

	public void setNovaVersaoExaSigla(String novaVersaoExaSigla) {
		this.novaVersaoExaSigla = novaVersaoExaSigla;
	}

	public Integer getNovaVersaoManSeq() {
		return novaVersaoManSeq;
	}

	public void setNovaVersaoManSeq(Integer novaVersaoManSeq) {
		this.novaVersaoManSeq = novaVersaoManSeq;
	}

	public DynamicDataModel<AelVersaoLaudo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelVersaoLaudo> dataModel) {
		this.dataModel = dataModel;
	}

	public AelVersaoLaudo getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelVersaoLaudo selecionado) {
		this.selecionado = selecionado;
	}
}