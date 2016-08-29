package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTipoItensAnamneseController extends ActionController {
	/**
     * 
     */
	private static final long serialVersionUID = 5686631634280751615L;
	
	private static final String MANTER_TIPO_ITENS_ANAMNESE = "ambulatorio-manterTipoItensAnamneseList";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private CseCategoriaProfissional categoriaProfissional;
	
	private MamTipoItemAnamneses tipoItemAnamnese;
	
	private boolean exibirFieldsResultado = false;
	
	private boolean exibirBotaoAdicionar;
	
	private DominioOperacoesJournal operacao;
	
	private Boolean situacao = false;
	
	private Boolean editandoTipoItemAnamnese = false;
	
	private List<MamTipoItemAnamneses> listaTipoItensAnamnese;

	private static final String ITENS_ANAMNSE_CRUD = "manterTipoItensAnamneseList";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	protected void recuperarListaTipoItensAnamnese() {
		
		this.listaTipoItensAnamnese = this.ambulatorioFacade.pesquisarListaTipoItemAnamnesesPorCategoriaProfissional(categoriaProfissional.getSeq());
		
		if (listaTipoItensAnamnese == null) {
			listaTipoItensAnamnese = new ArrayList<MamTipoItemAnamneses>();
		}
		
	}
	
	
	/**
	 * Cancela edição do tipo item anamnese
	 */
	public String cancelarEdicaoTipoItemAnamnese() {
		this.pesquisar();
		return ITENS_ANAMNSE_CRUD ;
	}
	
	public void pesquisar() {
		this.editandoTipoItemAnamnese = false;
		this.exibirBotaoAdicionar = true;
		this.exibirFieldsResultado = true;
		this.operacao = DominioOperacoesJournal.INS;
		this.situacao = true;
		this.setTipoItemAnamnese(new MamTipoItemAnamneses());
		this.tipoItemAnamnese.setObrigatorio(true);
		this.tipoItemAnamnese.setPermiteLivre(true);
		this.tipoItemAnamnese.setPermiteQuest(true);
		this.tipoItemAnamnese.setPermiteFigura(true);
		this.tipoItemAnamnese.setIdentificacao(true);
		this.tipoItemAnamnese.setPermiteNega(true);
		this.tipoItemAnamnese.setNotaAdicional(true);
		this.tipoItemAnamnese.setResExames(true);
		this.tipoItemAnamnese.setSituacao(DominioSituacao.getInstance(situacao));
		this.tipoItemAnamnese.setCategoriaProfissional(categoriaProfissional);
		recuperarListaTipoItensAnamnese();
	}
	
	public void limparPesquisa() {
		this.categoriaProfissional = null;
		this.exibirFieldsResultado = false;
		this.tipoItemAnamnese = null;
		this.situacao = true;
	}
	
	public List<CseCategoriaProfissional> obterListaCategoriaProfissional(String filtro) {
		return  this.ambulatorioFacade.pesquisarListaCategoriaProfissional(filtro);
	}
	
	public Long obterListaCategoriaProfissionalCount(Object filtro){
		return this.ambulatorioFacade.pesquisarListaCategoriaProfissionalCount(filtro);
	}
	
	public String salvar() {
		
		try {
			
			if (categoriaProfissional == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_CAMPO_OBRIGATORIO", 
						getBundle().getString("LABEL_CATEGORIA_PROFISSIONAL"));
				return null;
			}
			
			this.tipoItemAnamnese.setSituacao(DominioSituacao.getInstance(situacao));
			this.ambulatorioFacade.verificarEPersistirTipoItemAnamneses(tipoItemAnamnese);
			
			if (this.operacao.equals(DominioOperacoesJournal.INS)) {
				this.apresentarMsgNegocio(Severity.INFO, "MSG_TIPO_ITEM_ANAMNESE_INCLUIDO");
			} else if (this.operacao.equals(DominioOperacoesJournal.UPD)) {
				this.apresentarMsgNegocio(Severity.INFO, "MSG_TIPO_ITEM_ANAMNESE_ALTERADO");
			}
			this.editandoTipoItemAnamnese = false;
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return MANTER_TIPO_ITENS_ANAMNESE;
	}
	
	public String editarTipoItemAnamnese(MamTipoItemAnamneses tipoItemAnamneseAEditar) {
		this.tipoItemAnamnese = null;
		this.operacao = DominioOperacoesJournal.UPD;
		this.exibirBotaoAdicionar = false;
		this.editandoTipoItemAnamnese = true;
		this.situacao = tipoItemAnamneseAEditar.getSituacao().isAtivo();
		this.setTipoItemAnamnese(tipoItemAnamneseAEditar);
		return MANTER_TIPO_ITENS_ANAMNESE;
	}
	
	public CseCategoriaProfissional getCategoriaProfissional() {
		return categoriaProfissional;
	}
	
	public void setCategoriaProfissional(CseCategoriaProfissional categoriaProfissional) {
		this.categoriaProfissional = categoriaProfissional;
	}
	
	public boolean isExibirFieldsResultado() {
		return exibirFieldsResultado;
	}
	
	public void setExibirFieldsResultado(boolean exibirFieldsResultado) {
		this.exibirFieldsResultado = exibirFieldsResultado;
	}
	
	public MamTipoItemAnamneses getTipoItemAnamnese() {
		return tipoItemAnamnese;
	}
	
	public void setTipoItemAnamnese(MamTipoItemAnamneses tipoItemAnamnese) {
		this.tipoItemAnamnese = tipoItemAnamnese;
	}
	
	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}
	
	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}
	
	public Boolean getSituacao() {
		return situacao;
	}
	
	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}
	
	public List<MamTipoItemAnamneses> getListaTipoItensAnamnese() {
		return listaTipoItensAnamnese;
	}
	
	public void setListaTipoItensAnamnese(List<MamTipoItemAnamneses> listaTipoItensAnamnese) {
		this.listaTipoItensAnamnese = listaTipoItensAnamnese;
	}
	
	public boolean isExibirBotaoAdicionar() {
		return exibirBotaoAdicionar;
	}
	
	public void setExibirBotaoAdicionar(boolean exibirBotaoAdicionar) {
		this.exibirBotaoAdicionar = exibirBotaoAdicionar;
	}
	
	public Boolean getEditandoTipoItemAnamnese() {
		return editandoTipoItemAnamnese;
	}
	
	public void setEditandoTipoItemAnamnese(Boolean editandoTipoItemAnamnese) {
		this.editandoTipoItemAnamnese = editandoTipoItemAnamnese;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
}

