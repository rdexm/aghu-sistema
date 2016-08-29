package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author vinicius.silva
 */
public class ManterItensEvolucaoController extends ActionController {
	
	private static final long serialVersionUID = 5686631634280751615L;
	
	private static final String ITENS_EVOLUCAO_CRUD = "itensEvolucaoCRUD";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	private CseCategoriaProfissional categoriaProfissional;
	
	private MamTipoItemEvolucao tipoItemEvolucao;
	private Integer seqItenEvolucao;
	
	private boolean exibirFieldsResultado = false;
	private boolean exibirBotaoAdicionar;
	private boolean exibirColunaEdicao;
	
	private DominioOperacoesJournal operacao;
	
	private Boolean situacao = false;
	
	private List<MamTipoItemEvolucao> listaTipoEvolucao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	protected void recuperarListaTipoItensEvolucao() {
		this.listaTipoEvolucao = this.ambulatorioFacade.pesquisarListaTipoItemEvoulucaoPorCategoriaProfissional(categoriaProfissional.getSeq());
	}
	
	public void pesquisar() {
		seqItenEvolucao = null;
		this.exibirColunaEdicao = true;
		this.exibirBotaoAdicionar = true;
		this.exibirFieldsResultado = true;
		this.operacao = DominioOperacoesJournal.INS;
		this.situacao = true;
		this.tipoItemEvolucao = new MamTipoItemEvolucao();
		this.tipoItemEvolucao.setSituacao(DominioSituacao.getInstance(situacao));
		this.tipoItemEvolucao.setCategoriaProfissional(categoriaProfissional);
		
		this.tipoItemEvolucao.setObrigatorio(true);
		this.tipoItemEvolucao.setPermiteLivre(true);
		this.tipoItemEvolucao.setPermiteQuest(true);
		this.tipoItemEvolucao.setPermiteFigura(true);
		this.tipoItemEvolucao.setIdentificacao(true);
		this.tipoItemEvolucao.setPermiteNega(true);
		this.tipoItemEvolucao.setNotaAdicional(true);
		recuperarListaTipoItensEvolucao();
	}
	
	public String limparPesquisa() {
		this.exibirFieldsResultado = false;
		categoriaProfissional = null;
		this.situacao = true;
		return ITENS_EVOLUCAO_CRUD;
	}
	
	public Long obterListaCategoriaProfissionalCount(String filtro) {
		return this.ambulatorioFacade.pesquisarListaCategoriaProfissionalCount(filtro);
	}
	public List<CseCategoriaProfissional> obterListaCategoriaProfissional(String filtro) {
		return  this.returnSGWithCount(this.ambulatorioFacade.pesquisarListaCategoriaProfissional(filtro),obterListaCategoriaProfissionalCount(filtro));
	}
	
	public String salvar() {
		
		try {
			if(categoriaProfissional == null){
				apresentarMsgNegocio(Severity.ERROR, "MSG_CAMPO_OBRIGATORIO", getBundle().getString("LABEL_CATEGORIA_PROFISSIONAL"));
				return null;
			}
			if (this.operacao == DominioOperacoesJournal.UPD && !this.ambulatorioFacade.validarAlteracaoCamposInalteraveis(tipoItemEvolucao)) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TIPO_ITEM_EVOLUCAO_CAMPOS_INALTERAVEIS");
				return null;
			}
			
			this.tipoItemEvolucao.setSituacao(DominioSituacao.getInstance(situacao));
			this.ambulatorioFacade.salvarItenEvolucao(tipoItemEvolucao);
			
			if (this.operacao.equals(DominioOperacoesJournal.INS)) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TIPO_ITEM_EVOLUCAO_INCLUIDO");
			} else if (this.operacao.equals(DominioOperacoesJournal.UPD)) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TIPO_ITEM_EVOLUCAO_ALTERADO");
			}
			pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return ITENS_EVOLUCAO_CRUD;
	}

	public String cancelar(){
		this.pesquisar();
		seqItenEvolucao = null;
		return ITENS_EVOLUCAO_CRUD;
	}
	
	public String editar() {
		this.operacao = DominioOperacoesJournal.UPD;
		this.exibirBotaoAdicionar = false;
		this.exibirColunaEdicao = false;
		this.tipoItemEvolucao = null;
		this.tipoItemEvolucao = this.ambulatorioFacade.tipoItemEvolucaoPorSeq(seqItenEvolucao);
		this.situacao = tipoItemEvolucao.getSituacao().isAtivo();
		return ITENS_EVOLUCAO_CRUD;
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
	
	public Boolean getSituacao() {
		return situacao;
	}
	
	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public boolean isExibirBotaoAdicionar() {
		return exibirBotaoAdicionar;
	}

	public void setExibirBotaoAdicionar(boolean exibirBotaoAdicionar) {
		this.exibirBotaoAdicionar = exibirBotaoAdicionar;
	}

	public MamTipoItemEvolucao getTipoItemEvolucao() {
		return tipoItemEvolucao;
	}

	public void setTipoItemEvolucao(MamTipoItemEvolucao tipoItemEvolucao) {
		this.tipoItemEvolucao = tipoItemEvolucao;
	}

	public List<MamTipoItemEvolucao> getListaTipoEvolucao() {
		return listaTipoEvolucao;
	}

	public void setListaTipoEvolucao(List<MamTipoItemEvolucao> listaTipoEvolucao) {
		this.listaTipoEvolucao = listaTipoEvolucao;
	}

	public Integer getSeqItenEvolucao() {
		return seqItenEvolucao;
	}

	public void setSeqItenEvolucao(Integer seqItenEvolucao) {
		this.seqItenEvolucao = seqItenEvolucao;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}
	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public boolean isExibirColunaEdicao() {
		return exibirColunaEdicao;
	}

	public void setExibirColunaEdicao(boolean exibirColunaEdicao) {
		this.exibirColunaEdicao = exibirColunaEdicao;
	}
}