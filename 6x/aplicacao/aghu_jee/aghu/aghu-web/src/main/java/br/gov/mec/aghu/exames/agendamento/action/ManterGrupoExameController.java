package br.gov.mec.aghu.exames.agendamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesGrupoExameVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterGrupoExameController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -6153762937738535963L;

	private static final String GRUPO_EXAME_PESQUISA = "grupoExamePesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	
	// Variaveis que representam os campos do XHTML
	private AelGrupoExames grupoExame;
	private Integer codigo;
	private boolean situacao;
	private boolean situacaoGravar;
	
	// Flags que determinam comportamento da tela
	private boolean emEdicao;
	private boolean primeiraPesquisa;
	
	// Instancia para SuggestionBox de exame
	private VAelUnfExecutaExames exame;
	
	// Lista dos exames (Grupo tecnica de unidade funcional de exames)
	private List<ExamesGrupoExameVO> listaExamesGrupoExameVO;
	
	@Inject @Paginator
	private DynamicDataModel<ExamesGrupoExameVO> dataModel;
	
	private ExamesGrupoExameVO selecionado;

	private static final Enum[] fetchArgsInnerJoin = {AelGrupoExames.Fields.UNIDADE_FUNCIONAL};
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (this.emEdicao) {
			this.grupoExame = this.agendamentoExamesFacade.obterAelGrupoExamePeloId(codigo, fetchArgsInnerJoin, null);

			if(grupoExame == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			this.situacao = grupoExame.getSituacao() == DominioSituacao.A ? true : false;
			
			if(this.primeiraPesquisa){
				this.pesquisar();
			}
			
		} else {
			this.grupoExame = new AelGrupoExames();
			this.listaExamesGrupoExameVO = new ArrayList<ExamesGrupoExameVO>(0);
			this.situacao = true;
		}
		
		this.situacaoGravar = true;
		this.exame = null;
		
		return null;
	
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}

	public void pesquisar() {
		this.primeiraPesquisa = false;
		dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return this.agendamentoExamesFacade.countBuscarListaExamesGrupoExameVOPorCodigoGrupoExame(this.codigo);
	}

	@Override
	public List<ExamesGrupoExameVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		this.listaExamesGrupoExameVO = this.agendamentoExamesFacade.buscarListaExamesGrupoExameVOPorCodigoGrupoExame(firstResult, maxResult, orderProperty, asc, this.codigo);
		return listaExamesGrupoExameVO;
	}
	
	private boolean isValidaCamposRequeridosEmBranco(){
		boolean retorno = true;
		if(this.grupoExame != null){
			if(this.grupoExame.getAgendaExMesmoHor() && this.grupoExame.getCalculaTempo()){
				retorno = false;
				this.apresentarMsgNegocio(Severity.ERROR,"CAMPO_PROPS_GRUPO_INVALIDA");
			}
		}
		return retorno;
	}
	
	public String confirmar() {
		
		// Determina o tipo de mensagem de confirmacao
		final boolean isInclusao = this.grupoExame.getSeq() == null;
		
		try {
			if(isValidaCamposRequeridosEmBranco()){
				
				if(this.situacao) {
					this.grupoExame.setSituacao(DominioSituacao.A);
				} else {
					this.grupoExame.setSituacao(DominioSituacao.I); 
				}
				
				String mensagem = null;
				if (isInclusao) {
					this.grupoExame.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
					this.agendamentoExamesFacade.inserirGrupoExame(this.grupoExame);
					mensagem = "MENSAGEM_SUCESSO_INSERIR_GRUPO_EXAME";
				} else {
					this.agendamentoExamesFacade.alterarGrupoExame(this.grupoExame);
					mensagem = "MENSAGEM_SUCESSO_ALTERAR_GRUPO_EXAME";
				}
				this.apresentarMsgNegocio(Severity.INFO, mensagem);

				if(isInclusao){
					this.codigo = this.grupoExame.getSeq();
					this.emEdicao = true;
					this.iniciar();
				}
				
			} else {
				return null;
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
		
		return null;
	}
	
	/**
	 * Confirma a insercao/alteracao de um exame no grupo de exames
	 */
	public void confirmarExame(){
		
		try {

			AelGrupoExameUnidExame grpExameUnidExame = new AelGrupoExameUnidExame();
			
			AelGrupoExameUnidExameId id = new AelGrupoExameUnidExameId();
			
			AelUnfExecutaExames unfExecutaExames = examesFacade
					.obterAelUnidadeExecutoraExamesPorID(exame.getId().getSigla(), exame.getId().getManSeq(), exame.getId().getUnfSeq());
			
			id.setGexSeq(this.grupoExame.getSeq());
			id.setUfeEmaExaSigla(unfExecutaExames.getId().getEmaExaSigla());
			id.setUfeEmaManSeq(unfExecutaExames.getId().getEmaManSeq());
			id.setUfeUnfSeq(unfExecutaExames.getId().getUnfSeq().getSeq());

			grpExameUnidExame.setId(id);
			grpExameUnidExame.setGrupoExame(grupoExame);
			grpExameUnidExame.setUnfExecutaExame(unfExecutaExames);
			if(situacaoGravar) {
				grpExameUnidExame.setSituacao(DominioSituacao.A);
			} else {
				grpExameUnidExame.setSituacao(DominioSituacao.I);
			}
			
			// Persiste um grupo tecnica de unidade funcional de exames
			this.agendamentoExamesFacade.inserirGrupoExameUnidExame(grpExameUnidExame);

			// Refaz a pesquisa de grupo tecnica de unidade funcional de exames
			this.pesquisar();
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_GRUPO_EXAME_UNID_EXAME");
			
			this.exame = null;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método que realiza a ação do botão atualizar situação
	 */
	public void editarSituacao(ExamesGrupoExameVO item) {

		try {
			AelGrupoExameUnidExameId id = new AelGrupoExameUnidExameId();
			id.setGexSeq(this.codigo);
			id.setUfeEmaExaSigla(item.getUfeEmaExaSigla());
			id.setUfeEmaManSeq(item.getUfeEmaManSeq());
			id.setUfeUnfSeq(item.getUfeUnfSeq());
			
			final AelGrupoExameUnidExame grpExaUnidExa = this.agendamentoExamesFacade.obterGrupoExameUnidExamePorId(id);
			
			if (grpExaUnidExa != null) {
				
				if(!CoreUtil.igual(item.getVersion(), grpExaUnidExa.getVersion())){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return;
				}
				
				DominioSituacao situacao = item.getSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A;
				grpExaUnidExa.setSituacao(situacao);
				//Submete o procedimento para ser atualizado
				this.agendamentoExamesFacade.alterarGrupoExameUnidExame(grpExaUnidExa);
				//Atualiza botão da situação
				item.setSituacao(situacao);
				
				//Apresenta as mensagens de acordo
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_EXAME_UNID_EXAME");
				
			} else {
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			}

		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean isActive(ExamesGrupoExameVO item) {
		return (item.getSituacao() == DominioSituacao.A);
	}
	
	public void excluirExame()  {

		try {
			AelGrupoExameUnidExameId id = new AelGrupoExameUnidExameId( codigo, 
																		selecionado.getUfeEmaExaSigla(), 
																		selecionado.getUfeEmaManSeq(), 
																		selecionado.getUfeUnfSeq());

			this.agendamentoExamesFacade.excluirGrupoExameUnidExame(id);
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_GRUPO_TECNICA_UNF_EXAMES",selecionado.getDescricaoUsualExame());
			this.pesquisar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String cancelar() {
		this.grupoExame = null;
		this.codigo = null;
		this.emEdicao = false;
		return GRUPO_EXAME_PESQUISA;
	}

	public List<VAelUnfExecutaExames> pesquisarExame(String parametro) {
		List<VAelUnfExecutaExames> lista = this.examesFacade.pesquisarExamePorSiglaOuDescricao(parametro);
		return lista;
	}

	public Integer getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	public boolean isEmEdicao() {
		return emEdicao;
	}
	
	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
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

	public boolean isSituacao() {
		return situacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}

	public List<ExamesGrupoExameVO> getListaExamesGrupoExameVO() {
		return listaExamesGrupoExameVO;
	}

	public void setListaExamesGrupoExameVO(
			List<ExamesGrupoExameVO> listaExamesGrupoExameVO) {
		this.listaExamesGrupoExameVO = listaExamesGrupoExameVO;
	} 

	public boolean isSituacaoGravar() {
		return situacaoGravar;
	}

	public void setSituacaoGravar(boolean situacaoGravar) {
		this.situacaoGravar = situacaoGravar;
	}

	public boolean isPrimeiraPesquisa() {
		return primeiraPesquisa;
	}

	public void setPrimeiraPesquisa(boolean primeiraPesquisa) {
		this.primeiraPesquisa = primeiraPesquisa;
	}
 


	public DynamicDataModel<ExamesGrupoExameVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ExamesGrupoExameVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public ExamesGrupoExameVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ExamesGrupoExameVO selecionado) {
		this.selecionado = selecionado;
	}
}
