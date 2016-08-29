package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioAbrangenciaGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioResponsavelGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoRecomendacaoController extends ActionController {

	private static final long serialVersionUID = -1221282977588252360L;

	private static final String GRUPO_RECOMENDACAO_LIST = "grupoRecomendacaoList";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AelGrupoRecomendacao grupoRecomendacao;
	
	private List<AelGrupoRecomendacaoExame> listaGrupoRecomendacaoExame;
	
	private VAelExameMatAnalise vAelExameMatAnalise;
	
	private Boolean disabledBtAdicionar = Boolean.TRUE;
	
	private boolean iniciouTela = false;

	private static final Enum[] fetchArgsInnerJoin = {AelGrupoRecomendacao.Fields.SERVIDOR};

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(!iniciouTela){
			if (grupoRecomendacao != null && grupoRecomendacao.getSeq() != null) {
				grupoRecomendacao = examesFacade.obterAelGrupoRecomendacaoPeloId(grupoRecomendacao.getSeq(), fetchArgsInnerJoin, null);
				
				if(grupoRecomendacao == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
				
				// Sempre deve setar o idenficador da lista, independente de Inclusao ou Edicao.
				listaGrupoRecomendacaoExame = examesFacade.obterAelGrupoRecomendacaoExamesPorAelGrupoRecomendacao(grupoRecomendacao);
			} else {
				limpar();		
			}
		}
		
		iniciouTela = true;
		
		return null;
	
	}
	
	public String cancelar() {
		grupoRecomendacao = null;
		listaGrupoRecomendacaoExame = null;
		vAelExameMatAnalise = null;
		iniciouTela = false;
		return GRUPO_RECOMENDACAO_LIST;
	}
	
	public String gravar() {
		try {
			String msg;
			if (this.getGrupoRecomendacao().getSeq() == null) {
				msg = "MENSAGEM_SUCESSO_INCLUIDO_GRUPO_RECOMENDACAO";
			} else {
				msg = "MENSAGEM_SUCESSO_ALTERADO_GRUPO_RECOMENDACAO";
			}

			cadastrosApoioExamesFacade.gravarAelGrupoRecomendacao(grupoRecomendacao, listaGrupoRecomendacaoExame);
			
			String paramMsgNome = "";
			if (this.getGrupoRecomendacao() != null && StringUtils.isNotBlank(this.getGrupoRecomendacao().getDescricao())) {
				if (this.getGrupoRecomendacao().getDescricao().length() > 21) {
					paramMsgNome = this.getGrupoRecomendacao().getDescricao().substring(0, 20) + "...";
				} else {
					paramMsgNome = this.getGrupoRecomendacao().getDescricao();
				}
			}
			
			this.apresentarMsgNegocio(Severity.INFO, msg, paramMsgNome);
			
			return cancelar();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
	} 
	
	public void limpar() {
		this.initController();
	}
	
	public void adicionarExameMaterialAnalise() {
		if (this.getvAelExameMatAnalise() != null) {
			AelGrupoRecomendacaoExame item = new AelGrupoRecomendacaoExame();
			
			if (this.temAssociacaoComAelExamesMaterialAnalise(this.getvAelExameMatAnalise().getId())) {
				this.apresentarMsgNegocio(Severity.WARN, "ADVERTENCIA_GRUPO_REC_EXA_JAH_ASSOCIADO");
			} else {
				AelExamesMaterialAnalise exaMatAnalise = examesFacade.buscarAelExamesMaterialAnalisePorId(vAelExameMatAnalise.getId().getExaSigla(), vAelExameMatAnalise.getId().getManSeq());
				item.setExameMaterialAnalise(exaMatAnalise);
				listaGrupoRecomendacaoExame.add(item);
				
				this.initAdicaoExame();
			}			
		} else {
			this.apresentarMsgNegocio(Severity.WARN, "ADVERTENCIA_NENHUM_ITEM_SELECIOANDO");
		}
	}

	public void removerExameMaterialAnalise(AelGrupoRecomendacaoExame item) {
		listaGrupoRecomendacaoExame.remove(item);
	}

    /*
	public void posSelectionActionSuggestionBoxExaMatAnalise() {
		calculateEnableBtAdicionar();
	}
	
	public void posDeleteActionSuggestionBoxExaMatAnalise() {
		calculateEnableBtAdicionar();
	}
	
	protected void calculateEnableBtAdicionar() {
		if (this.getvAelExameMatAnalise() != null) {
			this.setDisabledBtAdicionar(Boolean.FALSE);
		} else {
			this.setDisabledBtAdicionar(Boolean.TRUE);			
		}
	}
	*/

    public void atualizaBtAdicionar(){
        if (this.getvAelExameMatAnalise() == null) {
            this.setDisabledBtAdicionar(Boolean.TRUE);
        } else {
            this.setDisabledBtAdicionar(Boolean.FALSE);
        }
    }


	protected void initController() {
		this.setGrupoRecomendacao(new AelGrupoRecomendacao());
		this.setListaGrupoRecomendacaoExame(new LinkedList<AelGrupoRecomendacaoExame>());
		this.initAdicaoExame();
		this.setDefaultValues();
	}
	
	protected void initAdicaoExame() {
		this.setDisabledBtAdicionar(Boolean.TRUE);
		this.setvAelExameMatAnalise(null);		
	}

	protected void setDefaultValues() {
		this.getGrupoRecomendacao().setResponsavel(DominioResponsavelGrupoRecomendacao.C);
		this.getGrupoRecomendacao().setAbrangencia(DominioAbrangenciaGrupoRecomendacao.I);
		this.getGrupoRecomendacao().setIndSituacao(DominioSituacao.A);
	}
	
	
	public List<VAelExameMatAnalise> obterExameMaterialAnalise(String objPesquisa) {
		try {
			return this.returnSGWithCount(cadastrosApoioExamesFacade.obterExameMaterialAnalise((String) objPesquisa),obterExameMaterialAnaliseCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<VAelExameMatAnalise>();
		}
	}

    public Long obterExameMaterialAnaliseCount(String objPesquisa) throws ApplicationBusinessException {
        return  this.cadastrosApoioExamesFacade.obterExameMaterialAnaliseCount(objPesquisa);
    }

	protected boolean temAssociacaoComAelExamesMaterialAnalise(AelExamesMaterialAnaliseId id) {
		boolean returnValue = false;
		
		if (id != null) {
			for (AelGrupoRecomendacaoExame grupoRecExame: this.getListaGrupoRecomendacaoExame()) {
				if ( id.equals(grupoRecExame.getExameMaterialAnalise().getId()) ) {
					// id jah existe na lista, nao deixa inserir repetido.
					returnValue = true;
					break;
				}
			}
		} else {
			// id null, nao deixa inserir associacao com id null.
			returnValue = true;
		}
		
		return returnValue;
	}

	public void setGrupoRecomendacao(AelGrupoRecomendacao grupoRecomendacao) {
		this.grupoRecomendacao = grupoRecomendacao;
	}

	public AelGrupoRecomendacao getGrupoRecomendacao() {
		return grupoRecomendacao;
	}

	public void setvAelExameMatAnalise(VAelExameMatAnalise vAelExameMatAnalise) {
		this.vAelExameMatAnalise = vAelExameMatAnalise;
	}

	public VAelExameMatAnalise getvAelExameMatAnalise() {
		return vAelExameMatAnalise;
	}

	public void setListaGrupoRecomendacaoExame(
			List<AelGrupoRecomendacaoExame> lista) {
		this.listaGrupoRecomendacaoExame = lista;
	}

	public List<AelGrupoRecomendacaoExame> getListaGrupoRecomendacaoExame() {
		return listaGrupoRecomendacaoExame;
	}

	public void setDisabledBtAdicionar(Boolean disabledBtAdicionar) {
		this.disabledBtAdicionar = disabledBtAdicionar;
	}

	public Boolean getDisabledBtAdicionar() {
		return disabledBtAdicionar;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}
}