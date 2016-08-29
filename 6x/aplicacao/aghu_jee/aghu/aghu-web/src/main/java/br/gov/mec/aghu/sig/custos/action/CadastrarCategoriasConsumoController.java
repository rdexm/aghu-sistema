package br.gov.mec.aghu.sig.custos.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastrarCategoriasConsumoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CadastrarCategoriasConsumoController.class);

	private static final long serialVersionUID = -1036600425549438959L;

	private boolean editMode;
	private boolean addMode;

	private Integer seq;
	private SigCategoriaConsumos categoriaConsumo;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@Inject
	private ManterCategoriasConsumoPaginatorController manterCategoriasConsumoPaginatorController;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	public void iniciar() {
	 

		if (avaliaFinalidadeTela()) {
			categoriaConsumo = this.custosSigCadastrosBasicosFacade.obterCategoriaConsumo(getSeq());
			setEditMode(true);
			setAddMode(false);
		} else {
			categoriaConsumo = new SigCategoriaConsumos();
			categoriaConsumo.setIndSituacao(DominioSituacao.A);
			setEditMode(false);
			setAddMode(true);
		}
	
	}

	private boolean avaliaFinalidadeTela() {
		return getSeq() != null;
	}

	public String voltar() {
		this.setSeq(null);
		this.manterCategoriasConsumoPaginatorController.getDataModel().reiniciarPaginator();
		return "manterCategoriasConsumo";
	}

	public void persistir() {
		if (categoriaConsumo.getSeq() == null) {
			gravar();
		} else {
			atualizar();
		}
	}

	private void atualizar() {
		try {
			getCustosSigCadastrosBasicosFacade().atualizarCategoriaConsumo(categoriaConsumo);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CATEGORIA_CONSUMO", categoriaConsumo.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e.getCause());
		}
	}

	private void gravar() {
		try {
			RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			getCustosSigCadastrosBasicosFacade().persistirCategoriaConsumo(categoriaConsumo, servidor);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_CATEGORIA_CONSUMO", categoriaConsumo.getDescricao());
			iniciar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e.getCause());
		}
	}

	public SigCategoriaConsumos getCategoriaConsumo() {
		return categoriaConsumo;
	}

	public void setCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) {
		this.categoriaConsumo = categoriaConsumo;
	}

	public void setCustosSigCadastrosBasicosFacade(ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade) {
		this.custosSigCadastrosBasicosFacade = custosSigCadastrosBasicosFacade;
	}

	public ICustosSigCadastrosBasicosFacade getCustosSigCadastrosBasicosFacade() {
		return custosSigCadastrosBasicosFacade;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setAddMode(boolean addMode) {
		this.addMode = addMode;
	}

	public boolean isAddMode() {
		return addMode;
	}

}
