package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarCuidadosInteresseCCIHPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2638955032766393399L;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	// Filtros aba Prescrição Médica
	private Integer codigoCuidadoUsual;
	private String descricaoCuidadoUsual;
	private DominioSimNao interesseCCIHCuidadoUsual;
	
	// Filtros aba Prescrição de Enfermagem
	private Short codigoCuidadoEnfermagem;
	private String descricaoCuidadoEnfermagem;
	private DominioSimNao interesseCCIHCuidadoEnfermagem;
	
	private String abaSelecionada;

	@Inject @Paginator
	private DynamicDataModel<MpmCuidadoUsual> dataModelCuidadoUsual;
	
	@Inject @Paginator
	private DynamicDataModel<EpeCuidados> dataModelCuidadoEnfermagem;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisarCuidadosUsuais() {
		this.dataModelCuidadoUsual.reiniciarPaginator();
	}
	
	public void pesquisarCuidadosEnfermagem() {
		this.dataModelCuidadoEnfermagem.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		Long count = null;
		if (this.abaSelecionada.equals("0")) {
			count = this.prescricaoMedicaFacade.listarCuidadosMedicosCount(this.codigoCuidadoUsual, this.descricaoCuidadoUsual,
					this.interesseCCIHCuidadoUsual != null ? this.interesseCCIHCuidadoUsual.isSim() : null);
		} else {
			count = this.prescricaoEnfermagemFacade.listarCuidadosEnfermagemCount(this.codigoCuidadoEnfermagem, this.descricaoCuidadoEnfermagem,
					this.interesseCCIHCuidadoEnfermagem != null ? this.interesseCCIHCuidadoEnfermagem.isSim() : null);
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public  List<?> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (this.abaSelecionada.equalsIgnoreCase("0")) {
			return this.prescricaoMedicaFacade.listarCuidadosMedicos(this.codigoCuidadoUsual, this.descricaoCuidadoUsual,
					this.interesseCCIHCuidadoUsual != null ? this.interesseCCIHCuidadoUsual.isSim() : null,
							firstResult, maxResult, orderProperty, asc);
			
		} else if (this.abaSelecionada.equalsIgnoreCase("1")) {
			return this.prescricaoEnfermagemFacade.listarCuidadosEnfermagem(this.codigoCuidadoEnfermagem, this.descricaoCuidadoEnfermagem,
					this.interesseCCIHCuidadoEnfermagem != null ? this.interesseCCIHCuidadoEnfermagem.isSim() : null,
							firstResult, maxResult, orderProperty, asc);
		}
		return null;
	}
	
	public void limparPesquisaCuidadosUsuais() {
		this.codigoCuidadoUsual = null;
		this.descricaoCuidadoUsual = null;
		this.interesseCCIHCuidadoUsual = null;
		this.dataModelCuidadoUsual.limparPesquisa();
	}
	
	public void limparPesquisaCuidadosEnfermagem() {
		this.codigoCuidadoEnfermagem = null;
		this.descricaoCuidadoEnfermagem = null;
		this.interesseCCIHCuidadoEnfermagem = null;
		this.dataModelCuidadoEnfermagem.limparPesquisa();
	}
	
	public void atualizarCuidadoUsual(MpmCuidadoUsual item) {
		this.prescricaoMedicaFacade.atualizarMpmCuidadoUsual(item);
		
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CUIDADO_INTERESSE");
	}
	
	public void atualizarCuidadoEnfermagem(EpeCuidados item) {
		this.prescricaoEnfermagemFacade.atualizarEpeCuidados(item);
		
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CUIDADO_INTERESSE");
	}
	
	// Getters and Setters
	public Integer getCodigoCuidadoUsual() {
		return codigoCuidadoUsual;
	}

	public void setCodigoCuidadoUsual(Integer codigoCuidadoUsual) {
		this.codigoCuidadoUsual = codigoCuidadoUsual;
	}

	public String getDescricaoCuidadoUsual() {
		return descricaoCuidadoUsual;
	}

	public void setDescricaoCuidadoUsual(String descricaoCuidadoUsual) {
		this.descricaoCuidadoUsual = descricaoCuidadoUsual;
	}

	public DominioSimNao getInteresseCCIHCuidadoUsual() {
		return interesseCCIHCuidadoUsual;
	}

	public void setInteresseCCIHCuidadoUsual(DominioSimNao interesseCCIHCuidadoUsual) {
		this.interesseCCIHCuidadoUsual = interesseCCIHCuidadoUsual;
	}

	public Short getCodigoCuidadoEnfermagem() {
		return codigoCuidadoEnfermagem;
	}

	public void setCodigoCuidadoEnfermagem(Short codigoCuidadoEnfermagem) {
		this.codigoCuidadoEnfermagem = codigoCuidadoEnfermagem;
	}

	public String getDescricaoCuidadoEnfermagem() {
		return descricaoCuidadoEnfermagem;
	}

	public void setDescricaoCuidadoEnfermagem(String descricaoCuidadoEnfermagem) {
		this.descricaoCuidadoEnfermagem = descricaoCuidadoEnfermagem;
	}

	public DominioSimNao getInteresseCCIHCuidadoEnfermagem() {
		return interesseCCIHCuidadoEnfermagem;
	}

	public void setInteresseCCIHCuidadoEnfermagem(
			DominioSimNao interesseCCIHCuidadoEnfermagem) {
		this.interesseCCIHCuidadoEnfermagem = interesseCCIHCuidadoEnfermagem;
	}

	public String getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(String abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public DynamicDataModel<MpmCuidadoUsual> getDataModelCuidadoUsual() {
		return dataModelCuidadoUsual;
	}

	public void setDataModelCuidadoUsual(
			DynamicDataModel<MpmCuidadoUsual> dataModelCuidadoUsual) {
		this.dataModelCuidadoUsual = dataModelCuidadoUsual;
	}

	public DynamicDataModel<EpeCuidados> getDataModelCuidadoEnfermagem() {
		return dataModelCuidadoEnfermagem;
	}

	public void setDataModelCuidadoEnfermagem(
			DynamicDataModel<EpeCuidados> dataModelCuidadoEnfermagem) {
		this.dataModelCuidadoEnfermagem = dataModelCuidadoEnfermagem;
	}

}
