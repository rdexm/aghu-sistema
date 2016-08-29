package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcGrupoProcedCirurgico;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class AgruparProcedimentoCirurgicoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 3121619287484273999L;

	private static final String AGRUPAR_GRUPO_PROCED = "agruparGrupoProcedimentoCirurgico";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AgruparGrupoProcedCirurgicoController agruparGrupoProcedCirurgicoController;
	
	private boolean ativo;

	// usado para pesquisar
	private MbcGrupoProcedCirurgico filtros;
	
	private List<MbcGrupoProcedCirurgico> lista;

	// Para Adicionar itens
	private MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico;

	private boolean editando;
	
	public AgruparProcedimentoCirurgicoController() {
		this.mbcGrupoProcedCirurgico = new MbcGrupoProcedCirurgico();
		this.filtros = new MbcGrupoProcedCirurgico();
	}

	/**
	 * Método usado no botão pesquisar
	 * 
	 * @return
	 */
	public void pesquisar() {
		this.lista = this.blocoCirurgicoCadastroApoioFacade.pesquisarMbcGrupoProcedCirurgico(filtros);
		this.editando = false;
		this.mbcGrupoProcedCirurgico = new MbcGrupoProcedCirurgico();
		this.ativo = true;
	}

	public void limpar() {
		this.ativo = false;
		this.filtros = new MbcGrupoProcedCirurgico();
		this.lista = null;
		this.editando = false;
		this.mbcGrupoProcedCirurgico = new MbcGrupoProcedCirurgico();
	}
	
	public void gravar() {

		mbcGrupoProcedCirurgico.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		final boolean novo = this.mbcGrupoProcedCirurgico.getSeq() == null;
		this.blocoCirurgicoCadastroApoioFacade.persistirMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);
		
		this.apresentarMsgNegocio(Severity.INFO,
				novo ? "MENSAGEM_GRUPO_PROCEDIMENTO_CIRURGICO_INSERT_SUCESSO" : "MENSAGEM_GRUPO_PROCEDIMENTO_CIRURGICO_UPDATE_SUCESSO",
						this.mbcGrupoProcedCirurgico.getDescricao());
		
		this.cancelarEdicao();

		this.mbcGrupoProcedCirurgico = new MbcGrupoProcedCirurgico();
		this.pesquisar();
	}

	public void editar(final Short seq) {
		this.editando = true;
		this.mbcGrupoProcedCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterMbcGrupoProcedCirurgicoPorChavePrimaria(seq);
	}

	public void cancelarEdicao() {
		this.editando = false;
//		this.blocoCirurgicoCadastroApoioFacade.refresh(this.mbcGrupoProcedCirurgico);
		this.mbcGrupoProcedCirurgico = new MbcGrupoProcedCirurgico();
	}

	public String redirecionarAgrupamentoGrupo(Short seq) {
		agruparGrupoProcedCirurgicoController.setSeqMbcGrupoProcedCirurgico(seq);
		return AGRUPAR_GRUPO_PROCED;
	}
	
	public void excluir(MbcGrupoProcedCirurgico item) {
		try {
			final MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterMbcGrupoProcedCirurgicoPorChavePrimaria(item.getSeq());
			this.blocoCirurgicoCadastroApoioFacade.removerMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRUPO_PROCEDIMENTO_CIRURGICO_DELETE_SUCESSO",
					mbcGrupoProcedCirurgico.getDescricao());
			
			this.pesquisar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void ativarInativar(final Short seq) {
		if (seq != null) {
			
			this.mbcGrupoProcedCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterMbcGrupoProcedCirurgicoPorChavePrimaria(seq);
			this.mbcGrupoProcedCirurgico.setSituacao((DominioSituacao.A.equals(this.mbcGrupoProcedCirurgico.getSituacao()) 
																				? DominioSituacao.I : DominioSituacao.A));

			this.blocoCirurgicoCadastroApoioFacade.persistirMbcGrupoProcedCirurgico(mbcGrupoProcedCirurgico);

			this.apresentarMsgNegocio(
					Severity.INFO,
					(DominioSituacao.A.equals(this.mbcGrupoProcedCirurgico.getSituacao()) ? "MENSAGEM_GRUPO_PROCEDIMENTO_CIRURGICO_INATIVADO_SUCESSO"
							: "MENSAGEM_GRUPO_PROCEDIMENTO_CIRURGICO_ATIVADO_SUCESSO"), this.mbcGrupoProcedCirurgico.getDescricao());

			this.mbcGrupoProcedCirurgico = new MbcGrupoProcedCirurgico();
		}
		this.pesquisar();
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(final boolean ativo) {
		this.ativo = ativo;
	}

	public List<MbcGrupoProcedCirurgico> getLista() {
		return lista;
	}

	public void setLista(final List<MbcGrupoProcedCirurgico> lista) {
		this.lista = lista;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(final boolean editando) {
		this.editando = editando;
	}

	public MbcGrupoProcedCirurgico getMbcGrupoProcedCirurgico() {
		return mbcGrupoProcedCirurgico;
	}

	public void setMbcGrupoProcedCirurgico(
			MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) {
		this.mbcGrupoProcedCirurgico = mbcGrupoProcedCirurgico;
	}

	public MbcGrupoProcedCirurgico getFiltros() {
		return filtros;
	}

	public void setFiltros(MbcGrupoProcedCirurgico filtros) {
		this.filtros = filtros;
	}
}
