package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MotivoCancelamentoPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = -3902991275596762896L;

	private static final String MOTIVO_CANCELAMENTO_CRUD = "motivoCancelamentoCrud";

	private static final String RELATORIO_MOTIVO_CANCELAMENTO_EXAMES = "exames-relatorioMotivoCancelamentoExamesPdf";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private RelatorioMotivoCancelamentoExamesController relatorioMotivoCancelamentoExamesController;
	
	private AelMotivoCancelaExames aelMotivoCancelaExames = new AelMotivoCancelaExames();
	private Short codigoMotivoCancelamentoExclusao;

	private DominioSimNao permiteIncluirResultado;

	@Inject @Paginator
	private DynamicDataModel<AelMotivoCancelaExames> dataModel;

	private AelMotivoCancelaExames selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.aelMotivoCancelaExames = new AelMotivoCancelaExames();
		this.permiteIncluirResultado = null;
	}
	
	@Override
	public Long recuperarCount() {
		if (permiteIncluirResultado != null) {
			if (permiteIncluirResultado.equals(DominioSimNao.S)) {
				aelMotivoCancelaExames.setIndPermiteIncluirResultado(true);
			} else {
				aelMotivoCancelaExames.setIndPermiteIncluirResultado(false);
			}
		}else{
			aelMotivoCancelaExames.setIndPermiteIncluirResultado(null);
		}
		
		return examesFacade.pesquisarMotivoCancelamentoCount(aelMotivoCancelaExames);
	}

	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		orderProperty = "descricao";
		if (permiteIncluirResultado != null) {
			if (permiteIncluirResultado.equals(DominioSimNao.S)) {
				aelMotivoCancelaExames.setIndPermiteIncluirResultado(true);
			} else {
				aelMotivoCancelaExames.setIndPermiteIncluirResultado(false);
			}
		}
		
		return examesFacade.pesquisarMotivoCancelamento(firstResult, maxResult, orderProperty, true,aelMotivoCancelaExames);
	}
	
	public void excluir() throws BaseException  {
		try {
			cadastrosApoioExamesFacade.removerMotivoCancelamento(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MOTIVO_CANCELAMENTO", selecionado.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MOTIVO_CANCELAMENTO_CRUD;
	}

	public String editar(){
		return MOTIVO_CANCELAMENTO_CRUD;
	}
	
	public void directPrint() throws BaseException, JRException, SystemException, IOException{
		this.relatorioMotivoCancelamentoExamesController.setMotivoCancelaExamesFiltro(this.aelMotivoCancelaExames);
		this.relatorioMotivoCancelamentoExamesController.directPrint();
	}

//	@Restrict("#{s:hasPermission('relatorioMotivosCancelamentoExames','imprimir')}")
	public String print() throws BaseException, JRException, SystemException, IOException {
		this.relatorioMotivoCancelamentoExamesController.setMotivoCancelaExamesFiltro(this.aelMotivoCancelaExames);
		return RELATORIO_MOTIVO_CANCELAMENTO_EXAMES;
	}
	
	public Short getCodigoMotivoCancelamentoExclusao() {
		return codigoMotivoCancelamentoExclusao;
	}

	public void setCodigoMotivoCancelamentoExclusao(
			Short codigoMotivoCancelamentoExclusao) {
		this.codigoMotivoCancelamentoExclusao = codigoMotivoCancelamentoExclusao;
	}

	public AelMotivoCancelaExames getAelMotivoCancelaExames() {
		return aelMotivoCancelaExames;
	}

	public void setAelMotivoCancelaExames(
			AelMotivoCancelaExames aelMotivoCancelaExames) {
		this.aelMotivoCancelaExames = aelMotivoCancelaExames;
	}

	public DominioSimNao getPermiteIncluirResultado() {
		return permiteIncluirResultado;
	}

	public void setPermiteIncluirResultado(DominioSimNao permiteIncluirResultado) {
		this.permiteIncluirResultado = permiteIncluirResultado;
	}

	public DynamicDataModel<AelMotivoCancelaExames> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelMotivoCancelaExames> dataModel) {
		this.dataModel = dataModel;
	}

	public AelMotivoCancelaExames getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelMotivoCancelaExames selecionado) {
		this.selecionado = selecionado;
	}
}