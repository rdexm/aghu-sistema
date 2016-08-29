package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.MbcUnidadeNotaSalaId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterNotaDeSalaUnidadeController extends ActionController implements ActionPaginator{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AssociarNotaDeSalaController associarNotaDeSalaController;
	
	@Inject
	private CadastroMateriaisImpressaoNotaSalaController cadastroMateriaisImpressaoNotaSalaController;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	//@Inject @Paginator
	//private DynamicDataModel<MbcUnidadeNotaSala> dataModel;

	private static final String CADASTRO_MATERIAIS_IMPRESSAO = "cadastroMateriaisImpressaoNotaSala";
	private static final String ASSOCIA_EQUIPAMENTOS = "associaEquipamentoNotaDeSala";
	
	private AghUnidadesFuncionais unidadeCirurgica;
	private List<MbcUnidadeNotaSala> notasSala;
	private MbcUnidadeNotaSala novaUnidSala;
	private Boolean situacaoNotaSala;
	
	private final Integer maxRegitrosProc = 100;
	

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
		//this.filtro.setUnidadeColeta(solicitacaoExameFacade.buscarAghUnidadesFuncionaisPorParametro(AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT.toString()));
		novaUnidSala = new MbcUnidadeNotaSala();
		situacaoNotaSala = Boolean.TRUE;
		novaUnidSala.setSituacao(DominioSituacao.getInstance(situacaoNotaSala));
	}
	
	public void pesquisar() {
		notasSala = blocoCirurgicoCadastroApoioFacade.buscarNotasSalaPorUnidadeCirurgica(this.unidadeCirurgica.getSeq());
	}
	
	@Override
	public List<MbcUnidadeNotaSala> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return notasSala = blocoCirurgicoCadastroApoioFacade.buscarNotasSalaPorUnidadeCirurgica(this.unidadeCirurgica.getSeq());
	}

	@Override
	public Long recuperarCount() {
		return blocoCirurgicoCadastroApoioFacade.buscarNotasSalaPorUnidadeCirurgicaCount(this.unidadeCirurgica.getSeq());
	}
	
	public boolean isAtiva(MbcUnidadeNotaSala unidSala){
		return unidSala.getSituacao().isAtivo();
	}
	
	public String redirecionarCadastroMateriaisNotaSala(Short unfSeq, Short seq) {
		cadastroMateriaisImpressaoNotaSalaController.setUnfSeq(unfSeq);
		cadastroMateriaisImpressaoNotaSalaController.setSeqp(seq);
		return CADASTRO_MATERIAIS_IMPRESSAO;
	}
	
	public void editar(MbcUnidadeNotaSala unidSala){

		boolean isAtivo = unidSala.getSituacao().isAtivo();

		try {

			if(isAtivo){
				unidSala.setSituacao(DominioSituacao.I);	
			}else{
				unidSala.setSituacao(DominioSituacao.A);
			}

			this.blocoCirurgicoCadastroApoioFacade.persistirNotaDeSala(unidSala);

			if(isAtivo){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INATIVACAO_NOTA_SALA");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATIVACAO_NOTA_SALA");
			}
		} catch (ApplicationBusinessException e) {

			apresentarExcecaoNegocio(e);

			if(unidSala.getSituacao().isAtivo()){
				unidSala.setSituacao(DominioSituacao.I);	
			}else{
				unidSala.setSituacao(DominioSituacao.A);
			}
		}
	}
	
	public String associarEquipamentos(MbcUnidadeNotaSala unidSala){
		associarNotaDeSalaController.setUnidadeNotaSala(unidSala);
		return ASSOCIA_EQUIPAMENTOS;
	}

	
	public void confirmar() {
		try {
			

			MbcUnidadeNotaSalaId notaId = this.novaUnidSala.getId();
			boolean isInclusao = (notaId == null || notaId.getSeqp()==null);	

			if (isInclusao){
				notaId = new MbcUnidadeNotaSalaId();
				notaId.setUnfSeq(this.unidadeCirurgica.getSeq());
			}

			novaUnidSala.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			novaUnidSala.setId(notaId);

			novaUnidSala.setSituacao(DominioSituacao.getInstance(this.getSituacaoNotaSala()));

			// Determina o tipo de mensagem de confirmação
			if (isInclusao) {
				this.blocoCirurgicoCadastroApoioFacade.persistirNotaDeSala(novaUnidSala);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_NOTA_SALA");
			}

			limpaCampos();
			//dataModel.reiniciarPaginator();
			notasSala = this.blocoCirurgicoCadastroApoioFacade.buscarNotasSalaPorUnidadeCirurgica(this.unidadeCirurgica.getSeq());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void limpaCampos(){
		novaUnidSala = new MbcUnidadeNotaSala();
		novaUnidSala.setSituacao(DominioSituacao.A);
		setSituacaoNotaSala(true);
		//dataModel.limparPesquisa();
	}
	/**
	 * Excluir
	 */
	public void excluir(Short unfSeqExc, Short seqpExc)  {
		try {
			MbcUnidadeNotaSalaId id = new MbcUnidadeNotaSalaId(unfSeqExc, seqpExc);
			this.blocoCirurgicoCadastroApoioFacade.excluirNotaDeSala(id);
			limpaCampos();
			//dataModel.reiniciarPaginator();
			notasSala = this.blocoCirurgicoCadastroApoioFacade.buscarNotasSalaPorUnidadeCirurgica(this.unidadeCirurgica.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_NOTA_SALA");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// Metodo para Suggestion Box de Unidade de Coleta (Unidade Funcional)
	public List<AghUnidadesFuncionais> obterUnidadesCirurgicas(String parametro){
		return this.returnSGWithCount(this.aghuFacade.listarAghUnidadesFuncionaisAtivasCirurgicas(parametro),obterUnidadesCirurgicasCount(parametro));
	}
	
    public Long obterUnidadesCirurgicasCount(String parametro){
        return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(parametro);
    }
	
	//Metodo para Suggestion Box de especialidades
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa){
		return this.returnSGWithCount(this.aghuFacade.listarEspecialidadePorNomeOuSigla((String)objPesquisa),pesquisarEspecialidadesCount(objPesquisa));
	}
	
    public Long pesquisarEspecialidadesCount(String objPesquisa){
    	return this.aghuFacade.listarEspecialidadePorNomeOuSiglaCount((String) objPesquisa);
    }
    
	//Metodo para Suggestion Box de Procedimentos cirurgicos
	public List<MbcProcedimentoCirurgicos> obterProcedimentoCirurgicos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarProcedimentoCirurgicos(objPesquisa, this.maxRegitrosProc),obterProcedimentoCirurgicosCount(objPesquisa));
	}
	
    public Long obterProcedimentoCirurgicosCount(String objPesquisa){
        return this.blocoCirurgicoFacade.listarProcedimentoCirurgicosCount(objPesquisa);
    }

	/*
	 * Getters and Setters abaixo...
	 */
	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public List<MbcUnidadeNotaSala> getNotasSala() {
		return notasSala;
	}

	public void setNotasSala(List<MbcUnidadeNotaSala> notasSala) {
		this.notasSala = notasSala;
	}

	public MbcUnidadeNotaSala getNovaUnidSala() {
		return novaUnidSala;
	}

	public void setNovaUnidSala(MbcUnidadeNotaSala novaUnidSala) {
		this.novaUnidSala = novaUnidSala;
	}

	public Boolean getSituacaoNotaSala() {
		return situacaoNotaSala;
	}

	public void setSituacaoNotaSala(Boolean situacaoNotaSala) {
		this.situacaoNotaSala = situacaoNotaSala;
	}
	
	/*public DynamicDataModel<MbcUnidadeNotaSala> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcUnidadeNotaSala> dataModel) {
		this.dataModel = dataModel;
	}*/
}