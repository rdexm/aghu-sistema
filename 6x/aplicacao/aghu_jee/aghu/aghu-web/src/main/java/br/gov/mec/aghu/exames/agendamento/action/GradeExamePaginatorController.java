package br.gov.mec.aghu.exames.agendamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.GradeExameVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGradeAgendaExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * Controller da tela de pesquisa de grades de agendamento
 * 
 * @author danilo.santos
 * 
 */

public class GradeExamePaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<GradeExameVO> dataModel;
	
	private GradeExameVO gradeExameSelecionado;

	private static final Log LOG = LogFactory.getLog(GradeExamePaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4375862282112364375L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private ManterGradeExameController manterGradeExameController;
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	
	private AelGradeAgendaExame gradeAgendamentoExame;
	
	private List<AelGradeAgendaExame> listaGradeAgendamentoExame = new ArrayList<AelGradeAgendaExame>();
	
	private Integer seq;
	
	private AghUnidadesFuncionais unidadeExecutora;
	
	private AelSalasExecutorasExames sala;
	
	private DominioSituacao situacao;
	
	private AelGrupoExames grupoExame;
	
	private VAelUnfExecutaExames exame;
	
	private RapServidores responsavel;

	private Short unfSeqGerado;
	
	private Integer seqpGerado;
	
	public GradeExamePaginatorController() {
		// TODO Auto-generated constructor stub
	}
	
	public void iniciar(){
	 

		if (this.unfSeqGerado!=null && seqpGerado !=null){
			limparPesquisa();
			this.unidadeExecutora = this.aghuFacade.obterAghUnidFuncionaisPeloId(this.unfSeqGerado);
			this.seq = this.seqpGerado;
			this.pesquisar();
			this.unfSeqGerado = null;
			this.seqpGerado = null;
		}
		else if(unidadeExecutora==null){
			// Obtem o usuario da unidade executora
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				usuarioUnidadeExecutora = null;
			}

			// Resgata a unidade executora associada ao usuario
			if (this.usuarioUnidadeExecutora != null) {
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}
		}
		this.situacao = DominioSituacao.A;
	
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparSuggestions(){
		this.sala = null;
		this.grupoExame = null;
		this.exame =  null;
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */	
	public void limparPesquisa() {
		this.seq = null;
		this.unidadeExecutora = null;
		this.situacao = null;
		this.sala = null;
		this.grupoExame = null;
		this.exame = null;
		this.responsavel = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	@Override
	public Long recuperarCount() {
		return agendamentoExamesFacade.pesquisarGradeExameCount(seq, unidadeExecutora, 
				situacao, sala, grupoExame, exame, responsavel);
	}
	
	@Override
	public List<GradeExameVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		List<GradeExameVO> lista = new ArrayList<GradeExameVO>();
			lista = agendamentoExamesFacade.pesquisarGradeExame(orderProperty, asc, seq, unidadeExecutora, 
						situacao, sala, grupoExame, exame, responsavel);
			if (lista == null) {
				return new ArrayList<GradeExameVO>();
			} else if(lista.size()>(firstResult+maxResult)){
				lista = lista.subList(firstResult, firstResult+maxResult);
			} else {
				lista = lista.subList(firstResult, lista.size());
			}
			return lista;
			
	}
	
	public String editar(GradeExameVO gradeExameEdicao){
		this.gradeAgendamentoExame = this.obterGradeAgendaExame(gradeExameEdicao);
		manterGradeExameController.setUnfSeq(gradeAgendamentoExame.getId().getUnfSeq());
		manterGradeExameController.setSeqp(gradeAgendamentoExame.getId().getSeqp());
		return "manterGradeExame";
	}
	
	public void excluir(GradeExameVO gradeExameExclusao){
		try {
			this.agendamentoExamesFacade.removerGradeAgendaExame(this.obterGradeAgendaExame(gradeExameExclusao));
			this.dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_GRADE_EXAME", gradeExameExclusao.getSeq());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}	
	}
	
	private AelGradeAgendaExame obterGradeAgendaExame(GradeExameVO gradeExameVO){
		AelGradeAgendaExameId idGrade = new AelGradeAgendaExameId(gradeExameVO.getGrade(), gradeExameVO.getSeq());
		return this.agendamentoExamesFacade.obterGradeExamePorChavePrimaria(idGrade);
	}
	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}
	
	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
		
			
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<AelSalasExecutorasExames> pesquisarSala(String parametro) {
		return this.examesFacade.pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(parametro, unidadeExecutora);
	}
	
	public List<AelGrupoExames> pesquisarGrupoExame(String parametro) {
		return this.examesFacade.pesquisarGrupoExamePorCodigoOuDescricaoEUnidade(parametro, unidadeExecutora);
	}
	
	public List<VAelUnfExecutaExames> pesquisarExame(String parametro) {
		List<VAelUnfExecutaExames> lista = this.examesFacade.pesquisarExamePorSiglaOuDescricao(parametro, this.unidadeExecutora == null ? null : this.unidadeExecutora.getSeq());
		return lista;
	}
	
	public List<RapServidores> pesquisarServidor(String parametro) {
			return registroColaboradorFacade.pesquisarServidores(parametro);
	}
	
	public AelGradeAgendaExame obterGradeAgendamentoExame(Short unfSeq, Integer seqp) {
		AelGradeAgendaExameId id = new AelGradeAgendaExameId(unfSeq, seqp);
		return agendamentoExamesFacade.obterGradeExamePorChavePrimaria(id);
	}	
	
	public Boolean verificarGradePossuiHorarios(Short unfSeq, Integer seqp) {
		if (agendamentoExamesFacade.obterCountHorarioExameDispTipoMarcacaoAtivaPorGrade(unfSeq, seqp) > 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public Boolean verificarGerarDisponibilidade(Short unfSeq, Integer seqp) {
		if (agendamentoExamesFacade.obterCountHorarioGradeExameGrade(unfSeq, seqp) > 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public AelGradeAgendaExame getGradeAgendamentoExame() {
		return gradeAgendamentoExame;
	}

	public void setGradeAgendamentoExame(AelGradeAgendaExame gradeAgendamentoExame) {
		this.gradeAgendamentoExame = gradeAgendamentoExame;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AelSalasExecutorasExames getSala() {
		return sala;
	}

	public void setSala(AelSalasExecutorasExames sala) {
		this.sala = sala;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public AelGrupoExames getGrupoExame() {
		return grupoExame;
	}

	public void setGrupoExame(AelGrupoExames grupoExame) {
		this.grupoExame = grupoExame;
	}

	public VAelUnfExecutaExames getExame() {
		return exame;
	}

	public void setExame(VAelUnfExecutaExames exame) {
		this.exame = exame;
	}

	public RapServidores getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	public List<AelGradeAgendaExame> getListaGradeAgendamentoExame() {
		return listaGradeAgendamentoExame;
	}

	public void setListaGradeAgendamentoExame(
			List<AelGradeAgendaExame> listaGradeAgendamentoExame) {
		this.listaGradeAgendamentoExame = listaGradeAgendamentoExame;
	}

	public IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return agendamentoExamesFacade;
	}

	public void setAgendamentoExamesFacade(
			IAgendamentoExamesFacade agendamentoExamesFacade) {
		this.agendamentoExamesFacade = agendamentoExamesFacade;
	}

	public void setSeqpGerado(Integer seqpGerado) {
		this.seqpGerado = seqpGerado;
	}

	public Integer getSeqpGerado() {
		return seqpGerado;
	}

	public void setUnfSeqGerado(Short unfSeqGerado) {
		this.unfSeqGerado = unfSeqGerado;
	}

	public Short getUnfSeqGerado() {
		return unfSeqGerado;
	}

	 


	public DynamicDataModel<GradeExameVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<GradeExameVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public GradeExameVO getGradeExameSelecionado() {
		return gradeExameSelecionado;
	}

	public void setGradeExameSelecionado(GradeExameVO gradeExameSelecionado) {
		this.gradeExameSelecionado = gradeExameSelecionado;
	}
}
