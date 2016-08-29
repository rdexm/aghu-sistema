package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.contaspagar.vo.FiltroTipoRespostaConsultoriaVO;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarTiposRespostasConsultoriaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -736588189582395257L;

	private static final String PAGE_MANTER_TIPOS_RESPOSTAS = "manterTiposRespostasConsultoria";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject @Paginator
	private DynamicDataModel<MpmTipoRespostaConsultoria> dataModel;
	
	private FiltroTipoRespostaConsultoriaVO filtro = new FiltroTipoRespostaConsultoriaVO();
	private MpmTipoRespostaConsultoria itemSelecionado;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return this.prescricaoMedicaFacade.listarTiposRespostasConsultoriaCount(this.filtro);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MpmTipoRespostaConsultoria> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.prescricaoMedicaFacade.listarTiposRespostasConsultoria(this.filtro, firstResult, maxResult, orderProperty, asc);
	}
	
	public String obterNomeTruncado(String nomePaciente) {
		if (nomePaciente.length() > 15) {
			return StringUtils.substring(nomePaciente, 0, 15).concat("...");
		}
		return nomePaciente;
	}

	public void excluir() {
		try {
			if (this.itemSelecionado != null) {
				String descricao = this.itemSelecionado.getDescricao();
				this.prescricaoMedicaFacade.excluirTipoRespostaConsultoria(this.itemSelecionado);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_TIPO_RESPOSTA_CONSULTORIA",
						descricao);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparCampos() {
		this.filtro = new FiltroTipoRespostaConsultoriaVO();
		this.dataModel.limparPesquisa();
	}

	public String inserirEditar() {
		return PAGE_MANTER_TIPOS_RESPOSTAS;
	}

	// getters & setters
	
	public DynamicDataModel<MpmTipoRespostaConsultoria> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmTipoRespostaConsultoria> dataModel) {
		this.dataModel = dataModel;
	}

	public FiltroTipoRespostaConsultoriaVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTipoRespostaConsultoriaVO filtro) {
		this.filtro = filtro;
	}

	public MpmTipoRespostaConsultoria getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MpmTipoRespostaConsultoria itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}
