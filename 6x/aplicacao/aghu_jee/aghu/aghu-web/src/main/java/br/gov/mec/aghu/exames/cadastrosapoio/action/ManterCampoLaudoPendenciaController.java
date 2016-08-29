package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.AelGrpTecnicaUnfExamesVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCampoLaudoPendenciaController extends ActionController {


	private static final Log LOG = LogFactory.getLog(ManterCampoLaudoPendenciaController.class);
	
	private static final long serialVersionUID = -6153762937738535963L;

	//private static final String MANTER_CAMPO_LAUDO_PENDENCIA = "manterCampoLaudoPendencia";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AelGrpTecnicaUnfExamesVO camposConsulta;
	
	// Parâmetros da conversação
	private String voltarPara;
	
	// Variaveis que representam os campos do XHTML
	private AelCampoLaudo campoLaudo;
	private AelGrupoTecnicaCampo aelGrupoTecnicaCampo = new AelGrupoTecnicaCampo();

	// Lista contendo campo laudo de pendencias 
	List<AelGrupoTecnicaCampo> listaCampoLaudoPendencia = new LinkedList<AelGrupoTecnicaCampo>();
	
	// Controla a exibição dos botões gravar, alterar e cancelar, assimo como o tipo de operação realizada
	private Boolean editandoItem = Boolean.FALSE;
	
	//parametros
	private Integer grtSeq;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private Short ufeUnfSeq;

    private String titulo;
	private AelGrupoTecnicaCampo itemExclusao;
	private AelGrupoTecnicaCampo itemEdicao;

	private boolean iniciouTela;

    private enum CampoLaudoPendenciaExceptionCode implements BusinessExceptionCode{
        MENSAGEM_CAMPO_LAUDO_OBRIGATORIO,
        MENSAGEM_TITULO_OBRIGATORIO;
    }


    @PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(iniciouTela){
			return null;
		}
		iniciouTela = true;

		if (grtSeq != null && StringUtils.isNotBlank(ufeEmaExaSigla) && ufeUnfSeq != null) {
			camposConsulta = examesFacade.obterAelGrpTecnicaUnfExamesVO(grtSeq, ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq);

			if(camposConsulta == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
			
			carregarLista();
		}
		
		return null;
	
	}

	private void carregarLista() {
		//carregar lista
		listaCampoLaudoPendencia = examesFacade.pesquisarCampoLaudoPendencia(grtSeq, ufeEmaExaSigla, ufeUnfSeq);
	}

    private void validaCampoObrigatorio() throws ApplicationBusinessException{
        if (this.getTitulo() == null){
            throw new ApplicationBusinessException(CampoLaudoPendenciaExceptionCode.MENSAGEM_TITULO_OBRIGATORIO);
        }

        if (this.campoLaudo == null){
            throw new ApplicationBusinessException(CampoLaudoPendenciaExceptionCode.MENSAGEM_CAMPO_LAUDO_OBRIGATORIO);
        }
    }

	public void confirmar() {
		
		try {
            this.validaCampoObrigatorio();

            this.aelGrupoTecnicaCampo.setTitulo(this.getTitulo());

			// Para operação de inclusão associar os seguintes campos:
			if(!editandoItem){ 
				aelGrupoTecnicaCampo.setCampoLaudo(campoLaudo);
				aelGrupoTecnicaCampo.setGrupoTecnicaUnfExames(examesFacade.buscarAelGrpTecnicaUnfExamesPorId(
																camposConsulta.obterAelGrpTecnicaUnfExamesId()));
			}
			
			cadastrosApoioExamesFacade.persistirCampoLaudoPendencia(this.aelGrupoTecnicaCampo);

			String mensagem = null;
			if (this.editandoItem) {
				mensagem = "MENSAGEM_SUCESSO_ALTERAR_CAMPO_LAUDO_PENDENCIA";
			} else {
				mensagem = "MENSAGEM_SUCESSO_INSERIR_CAMPO_LAUDO_PENDENCIA";
			}
			this.apresentarMsgNegocio(Severity.INFO, mensagem, aelGrupoTecnicaCampo.getCampoLaudo().getNome());

			this.limparCampos();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage());
		}
		
		carregarLista();
	}
	
	public void excluir() {
		try {
			cadastrosApoioExamesFacade.excluirCampoLaudoPendencia(itemExclusao.getId());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_CAMPO_LAUDO_PENDENCIA", itemExclusao.getCampoLaudo().getNome());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		carregarLista();
	}
	
	public void editar(){
		this.aelGrupoTecnicaCampo = getItemEdicao();
		this.campoLaudo = getItemEdicao().getCampoLaudo();
        this.titulo = getItemEdicao().getTitulo();
		this.editandoItem = Boolean.TRUE;
	}
	
	/**
	 * Verifica se o item AEL_GRP_TECNICA_CAMPOS foi selecionado na lista.
	 */
	public boolean isItemSelecionado(final AelGrupoTecnicaCampo aelGrupoTecnicaCampo){
		if(this.aelGrupoTecnicaCampo != null && this.aelGrupoTecnicaCampo.equals(aelGrupoTecnicaCampo)){
			return true;
		}
		return false;
	}
	
	public void cancelarEdicao(){
		this.limparCampos();
	}
	
	public String voltar() {
		iniciouTela = false;
		this.limparCampos();
		return this.voltarPara;
	}
	
	public List<AelCampoLaudo> pesquisarCampoLaudoSuggestion(String descr){
		return examesFacade.pesquisarAelCampoLaudoSuggestion((String) descr);
	}
	
	private void limparCampos() {
		this.aelGrupoTecnicaCampo = new AelGrupoTecnicaCampo();
		this.campoLaudo = null;
        this.titulo = null;
		this.editandoItem = Boolean.FALSE;
	}
	
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public AelGrupoTecnicaCampo getAelGrupoTecnicaCampo() {
		return aelGrupoTecnicaCampo;
	}

	public void setAelGrupoTecnicaCampo(
			AelGrupoTecnicaCampo aelGrupoTecnicaCampo) {
		this.aelGrupoTecnicaCampo = aelGrupoTecnicaCampo;
	}

	public List<AelGrupoTecnicaCampo> getListaCampoLaudoPendencia() {
		return listaCampoLaudoPendencia;
	}

	public void setListaCampoLaudoPendencia(
			List<AelGrupoTecnicaCampo> listaCampoLaudoPendencia) {
		this.listaCampoLaudoPendencia = listaCampoLaudoPendencia;
	}

	public Integer getGrtSeq() {
		return grtSeq;
	}

	public void setGrtSeq(Integer grtSeq) {
		this.grtSeq = grtSeq;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public AelGrpTecnicaUnfExamesVO getCamposConsulta() {
		return camposConsulta;
	}

	public void setCamposConsulta(AelGrpTecnicaUnfExamesVO camposConsulta) {
		this.camposConsulta = camposConsulta;
	}

	public AelGrupoTecnicaCampo getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(AelGrupoTecnicaCampo itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

    public AelGrupoTecnicaCampo getItemEdicao() {
        return itemEdicao;
    }

    public void setItemEdicao(AelGrupoTecnicaCampo itemEdicao) {
        this.itemEdicao = itemEdicao;
    }


	public Boolean getEditandoItem() {
		return editandoItem;
	}

	public void setEditandoItem(Boolean editandoItem) {
		this.editandoItem = editandoItem;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}