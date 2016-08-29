package br.gov.mec.aghu.exames.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMensagem;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelExigenciaExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterRestricaoExamesController extends ActionController {

	private static final long serialVersionUID = 4375862282112364375L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private VAelUnfExecutaExames vAelUnfExecutaExames;
	private AelUnfExecutaExames unfExecutaExames;
	private AghUnidadesFuncionais unidadeFuncional;
	private AelExigenciaExame exigenciaExame;
	private	Boolean situacao;
	private Integer seq;

	private static final Enum[] fetchArgsInnerJoin={AelExigenciaExame.Fields.UNIDADE_FUNCIONAL};

	private static final String MANTER_RESTRICAO_EXAMES_LIST = "manterRestricaoExamesList";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(seq != null) {
			exigenciaExame = this.examesFacade.obterAelExigenciaExame(seq, fetchArgsInnerJoin, null);
			
			if(exigenciaExame == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			unidadeFuncional = exigenciaExame.getUnidadeFuncional();
			situacao = exigenciaExame.getIndSituacao().isAtivo();
			vAelUnfExecutaExames = this.examesFacade.obterVAelUnfExecutaExames(exigenciaExame.getUnfExecutaExames().getId().getEmaExaSigla(), exigenciaExame.getUnfExecutaExames().getId().getEmaManSeq(), exigenciaExame.getUnfExecutaExames().getId().getUnfSeq().getSeq());
			
		} else {
			exigenciaExame = new AelExigenciaExame();
			exigenciaExame.setTipoMensagem(DominioTipoMensagem.R);
			unidadeFuncional = null;
			vAelUnfExecutaExames = null;
			situacao = true;
		}
		
		return null;
	
	}

	public String gravar() {
		try {
			Boolean edicao = false;
			exigenciaExame.setIndSituacao(DominioSituacao.getInstance(situacao));
			exigenciaExame.setUnidadeFuncional(unidadeFuncional);
			unfExecutaExames = this.examesFacade.obterAelUnfExecutaExames(vAelUnfExecutaExames.getId().getSigla(), vAelUnfExecutaExames.getId().getManSeq(), vAelUnfExecutaExames.getId().getUnfSeq());
			exigenciaExame.setUnfExecutaExames(unfExecutaExames);
			
			if(exigenciaExame.getSeq() != null) {
				edicao = true;
			}
			
			this.examesFacade.persistirAelExigenciaExame(exigenciaExame);
			
			if(!edicao) {
				this.apresentarMsgNegocio(Severity.INFO,"RESTRICAO_EXAME_INCLUIDO_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"RESTRICAO_EXAME_ALTERADO_SUCESSO");
			}
			
			this.seq = null;
			return cancelar();
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		seq = null;
		exigenciaExame = null;
		return MANTER_RESTRICAO_EXAMES_LIST;
	}
	
	public List<VAelUnfExecutaExames> pesquisarPorSiglaOuMaterialOuExameOuUnidade(String filtro){
		return this.examesFacade.pesquisarPorSiglaOuMaterialOuExameOuUnidade((String) filtro);
	}
	
	public Long pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount(Object filtro){
		return this.examesFacade.pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount((String) filtro);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(String filtro) {
		return this.aghuFacade.listarAghUnidadesFuncionais(filtro);
	}

	public VAelUnfExecutaExames getvAelUnfExecutaExames() {
		return vAelUnfExecutaExames;
	}

	public void setvAelUnfExecutaExames(VAelUnfExecutaExames vAelUnfExecutaExames) {
		this.vAelUnfExecutaExames = vAelUnfExecutaExames;
	}

	public AelUnfExecutaExames getUnfExecutaExames() {
		return unfExecutaExames;
	}

	public void setUnfExecutaExames(AelUnfExecutaExames unfExecutaExames) {
		this.unfExecutaExames = unfExecutaExames;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AelExigenciaExame getExigenciaExame() {
		return exigenciaExame;
	}

	public void setExigenciaExame(AelExigenciaExame exigenciaExame) {
		this.exigenciaExame = exigenciaExame;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
